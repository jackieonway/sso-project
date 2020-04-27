package com.github.jackieonway.controller;

import com.github.jackieonway.config.AuthorityPropertyEditor;
import com.github.jackieonway.config.SplitCollectionEditor;
import com.github.jackieonway.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("clients")
public class ClientsController {
    @Autowired
    private JdbcClientDetailsService jdbcClientDetailsService;
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
        if (newClient == null) {
            String pwdEncode = Utils.passwordEncoder(clientDetails.getClientSecret());
            clientDetails.setClientSecret(pwdEncode);
        } else {
            Set<String> autoApproval = new HashSet<>();
            autoApproval.add("false");
            clientDetails.setAutoApproveScopes(autoApproval);
            String pwdEncode = Utils.passwordEncoder(clientDetails.getClientSecret());
            clientDetails.setClientSecret(pwdEncode);
            jdbcClientDetailsService.addClientDetails(clientDetails);
        }

        if (!clientDetails.getClientSecret().isEmpty()) {
            jdbcClientDetailsService.updateClientDetails(clientDetails);
        }
        return "redirect:/";
    }

    @RequestMapping(value="{client.clientId}/delete",method = RequestMethod.GET)
    public String deleteClient(@ModelAttribute BaseClientDetails clientDetails,@PathVariable("client.clientId") String id){
        jdbcClientDetailsService.removeClientDetails(jdbcClientDetailsService.loadClientByClientId(id).getClientId());
        return "redirect:/";
    }
}
