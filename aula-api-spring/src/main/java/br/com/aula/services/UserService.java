package br.com.aula.services;

import br.com.aula.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserService  implements UserDetailsService {

    @Autowired
    UserRepository repository;
    private Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Findind one user by name "+ username +"!");
        var user = repository.findByUsername(username);
        if(user != null){
            return user;
        } else {
            throw new UsernameNotFoundException("Usuário "+ username +" não encontrado");
        }
    }
}
