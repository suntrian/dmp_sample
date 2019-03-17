package com.quantchi.web.security;

import com.quantchi.web.account.entity.UserAccount;
import com.quantchi.web.account.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.mgt.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//@Configuration
public class ShiroConfiguration {

    @Component
    public static class UserRealm extends AuthorizingRealm {

        @Override
        public boolean supports(AuthenticationToken token) {
            return token instanceof JwtPasswordKapachaToken;
        }

        @Autowired
        private UserService userService;

        @Override
        protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
            UserAccount userAccount = (UserAccount) principalCollection.getPrimaryPrincipal();
            return null;
        }

        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
                throws AuthenticationException {
            UsernamePasswordToken token = new UsernamePasswordToken();
            return null;
        }
    }

    @Component
    public static class JwtRememberMeManager extends AbstractRememberMeManager{

        @Override
        protected void forgetIdentity(Subject subject) {

        }

        @Override
        protected void rememberSerializedIdentity(Subject subject, byte[] bytes) {

        }

        @Override
        protected byte[] getRememberedSerializedIdentity(SubjectContext subjectContext) {
            return new byte[0];
        }

        @Override
        public void forgetIdentity(SubjectContext subjectContext) {

        }
    }

    @Bean
    public CredentialsMatcher credentialsMatcher(){
        return new PasswordMatcher();
    }


    @Bean
    public SecurityManager securityManager(@Autowired CredentialsMatcher credentialsMatcher, @Autowired UserRealm realm, @Autowired RememberMeManager rememberMeManager){
        realm.setCredentialsMatcher(credentialsMatcher);
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setRememberMeManager(rememberMeManager);
        /*
         * 关闭shiro自带的session，详情见文档
         * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Autowired SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new JwtShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/account/login");
        return shiroFilterFactoryBean;
    }

}
