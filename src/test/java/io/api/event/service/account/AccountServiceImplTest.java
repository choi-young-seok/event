package io.api.event.service.account;

import io.api.event.config.TestConstants;
import io.api.event.domain.entity.account.Account;
import io.api.event.domain.entity.account.AccountRole;
import io.api.event.repository.account.AccountRepository;
import io.api.event.util.common.TestDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles(TestConstants.TEST)
class AccountServiceImplTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @TestDescription("Spring security의 UserDetailsService를 이용한 권한 인증 여부 확인")
    @DisplayName("Account Service : 로그인 요청")
    public void findByUserName_Test(){
        // Given
        String email = "rcn115@naver.com";
        String password = "1234";

        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        Account createdAccount = this.accountService.saveAccount(account);

        // When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertThat(userDetails.getPassword()).isEqualTo(createdAccount.getPassword());
    }

    @Test
    @TestDescription("요청 파라미터에 해당하는 Account 정보가 없는 경우 발생 하는 예외 처리")
    @DisplayName("Account Service : 유효하지 못한 로그인 요청의 예외 발생")
    public void findByUserName_Exception_Test(){
        // Given
        String email = "rcn115@naver.com";

        try {
            accountService.loadUserByUsername(email);
            fail("supposed to be failed");
        } catch (UsernameNotFoundException e) {
            assertThat(e.getMessage()).containsSequence(email);
        }
    }
}