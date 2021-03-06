package de.gruppe2.agamoTTTo.security.configuration;

import de.gruppe2.agamoTTTo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterInvocation;

/**
 * This class is used for enabling Spring Security for our application and setting up its configuration.
 * Plus: Security based on method annotations (@PreAuthorize/@PostAuthorize) is enabled.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserService userDetailsService;

    private BCryptPasswordEncoder passwordEncoder;

    private SecurityExpressionHandler<FilterInvocation> webExpressionHandler;

    private SessionRegistry sessionRegistry;

    @Autowired
    public WebSecurityConfiguration(UserService userDetailsService,
                                    BCryptPasswordEncoder passwordEncoder,
                                    SecurityExpressionHandler<FilterInvocation> webExpressionHandler,
                                    SessionRegistry sessionRegistry) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.webExpressionHandler = webExpressionHandler;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Set the service which is responsible for finding the user in the database
     * and set the necessary passwordEncoder for comparing the entered with the
     * saved password.
     *
     * @param auth SecurityBuilder for JDBC based authentication used to create an AuthenticationManager.
     * @throws Exception If an error occurs while adding the UserDetailsService.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {


        // Set the service which finds a user in the database and set the passwordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * Finally configure the whole Security Configuration for this application.
     *
     * @param http Used to configure specific HTTP requests.
     * @throws Exception If any request can't be configured properly.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Add the ExpressionHandler
        http.authorizeRequests().expressionHandler(webExpressionHandler);

        // The login page does not require an user to be logged in
        http.authorizeRequests().antMatchers("/").permitAll();

        // When the user is logged in as ROLE_X,
        // but wants to access a page that requires ROLE_Y,
        // AccessDeniedException will be thrown.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/accessDenied");

        // Configuration of the session
        http.sessionManagement()
                .maximumSessions(100) // Limit maximum session to a high number, so that a user can be logged on various devices
                .expiredUrl("/?logout=invoked") // Set the URL if the session has expired
                .maxSessionsPreventsLogin(false) // Don't prevent the user from logging in, if the maximum number of session is reached
                .sessionRegistry(sessionRegistry); // Necessary to access sessions stored on the server

        // Configuration for login and logout
        http.authorizeRequests().and().formLogin()
                .loginPage("/") // Page with the login form
                .loginProcessingUrl("/j_spring_security_check") // Submit URL of login form (provided by Spring Security).
                .defaultSuccessUrl("/home") // Redirect to this page, when login was successful.
                .failureUrl("/?loginError=true") // Redirect to this page, when login failed.
                .usernameParameter("email") // Define the entered e-mail as the username parameter
                .passwordParameter("password") // Define the entered password as the password parameter
                .and().logout() // Configuration for logout
                .logoutUrl("/logout") // Page for logout (provided by Spring Security).
                .logoutSuccessUrl("/?logout=successful"); // Redirect to this page, when logout was successful.
    }
}
