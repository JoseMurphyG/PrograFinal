package com.example.chatbot.controller;

import com.example.chatbot.model.ClientModel;
import com.example.chatbot.service.ClientService;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Clients")
public class ClientController {
   @Autowired
   private ClientService clientService;
   
   @GetMapping
   public ArrayList<ClientModel> getClient(){
       return this.clientService.getClient();
   }
   
   @PostMapping
   public ClientModel saveClient(@RequestBody ClientModel client){
       return this.clientService.saveClient(client);
   }
   
   @GetMapping(path = "/{id}")
   public Optional<ClientModel> getClientById(@PathVariable("id") Long id){
       return this.clientService.getById(id);
   }
   
   @PutMapping(path = "/{id}")
   public ClientModel updateClientById(@RequestBody @PathVariable("id") ClientModel request, Long id){
       return this.clientService.updateById(request, id);
   }
   
   @DeleteMapping(path = "/{id}")
   public String deleteById(@PathVariable("id") Long id){
       boolean ok = this.clientService.deleteClient(id);
       
       if(ok == true){
           return "El cliente con el id: " + id +" ha sido borrado";
       }else{
         return "Error: no se puede borrar el cliente";  
       }
   }
}
