package com.github.jackieonway.controller;

import com.github.jackieonway.config.AuthorityPropertyEditor;
import com.github.jackieonway.config.SplitCollectionEditor;
import com.github.jackieonway.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("clients")
public class ClientsController {
    @Autowired
    private JdbcClientDetailsService jdbcClientDetailsService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @InitBinder
    public void initBinder(WebDataBinder binder){

        binder.registerCustomEditor(Collection.class,new SplitCollectionEditor(Set.class,","));
        binder.registerCustomEditor(GrantedAuthority.class,new AuthorityPropertyEditor());

    }

    @RequestMapping(value="/form",method= RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_OAUTH_ADMIN')")
    public String showEditForm(@RequestParam(value="client",required=false)String clientId, Model model){

        ClientDetails clientDetails;
        if(clientId !=null){
            clientDetails=jdbcClientDetailsService.loadClientByClientId(clientId);
        }
        else{
            clientDetails =new BaseClientDetails();
        }

        model.addAttribute("clientDetails",clientDetails);
        return "form.html";
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_OAUTH_ADMIN')")
    public String editClient(
            @ModelAttribute BaseClientDetails clientDetails,
            @RequestParam(value = "newClient", required = false) String newClient) {
        if (Objects.isNull(newClient)) {
            if (!StringUtils.isEmpty(clientDetails.getClientSecret())){
                String pwdEncode = Utils.passwordEncoder(clientDetails.getClientSecret());
                jdbcClientDetailsService.updateClientSecret(clientDetails.getClientId(),pwdEncode);
            }
        } else {
            if (StringUtils.isEmpty(clientDetails.getClientSecret())){
               throw new RuntimeException("资源客户端密钥不能为空");
            }
            String pwdEncode = Utils.passwordEncoder(clientDetails.getClientSecret());
            clientDetails.setClientSecret(pwdEncode);
            jdbcClientDetailsService.addClientDetails(clientDetails);
            updateAutoApproval("false", clientDetails.getClientId());
            return "redirect:/";
        }
        jdbcClientDetailsService.updateClientDetails(clientDetails);
        updateAutoApproval("false", clientDetails.getClientId());
        return "redirect:/";
    }

    /**
     * 更新自动授权字段
     * @param autoapprove 自动授权
     * @param clientId 客户端id
     * @author  Jackie
     * @date  2020/4/27 16:01
     * @since 1.0
     * @see ClientsController
     */
    private void updateAutoApproval(String autoapprove , String clientId) {
        String sql = "UPDATE oauth_client_details SET autoapprove = '" + autoapprove + "'  WHERE client_id = '"+ clientId  +"';";
        jdbcTemplate.execute(sql);
    }

    @RequestMapping(value="{client.clientId}/delete",method = RequestMethod.GET)
    public String deleteClient(@PathVariable("client.clientId") String id){
        jdbcClientDetailsService.removeClientDetails(jdbcClientDetailsService.loadClientByClientId(id).getClientId());
        return "redirect:/";
    }

    @RequestMapping(value="{client.clientId}/autoApprove",method = RequestMethod.GET)
    public String cancelAutoApprove(@PathVariable("client.clientId") String clientId,String autoApprove){
        updateAutoApproval(autoApprove, clientId);
        return "redirect:/";
    }
}
