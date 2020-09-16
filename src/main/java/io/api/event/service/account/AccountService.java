package io.api.event.service.account;

import io.api.event.domain.entity.account.Account;
import io.api.event.domain.entity.account.AccountRole;
import io.api.event.repository.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    /**
     * Application에서 정의한 Account domain을 Spring security에서 정의한 UsertDetail Interface로 변환
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(userName)
                /**
                 * 요청 파라미터에 해당하는 Account 객체 조회 실패 시 오류 반환 처리
                 * - userName(account.email)에 해당 하는 Account 객체 조회 실패 시 Null을 반환 하므로
                 * - Srping security의 UsernameNotFoundException객체를 통해 정의된 오류를 반환한다.
                 */
                .orElseThrow(() -> new UsernameNotFoundException(userName));

        /**
         * Srping security의 User객체를 이용하여 Account 객체를 UserDetails 객체로 변환
         *  - UserDetails interface로 객체 변환 처리를 구현할 경우 모든 메소드를 구혆해야하므로,
         *  - UserDetails의 User객체를 이용하여 Account객체를 Spring Security의 UserDetails 객체로 변환한다.
         */
        return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
    }

    // Mapping AccountRole to GrantedAuthority
    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
       return roles.stream()
               .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
               .collect(Collectors.toSet());
    }
}

