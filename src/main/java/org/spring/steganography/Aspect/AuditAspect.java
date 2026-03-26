package org.spring.steganography.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.spring.steganography.Service.AuditService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @AfterReturning(pointcut = "execution(* org.spring.steganography.Service.AdminService.*(..))")
        public void adminActionLog(JoinPoint joinPoint){
            String performedBy="SYSTEM";
            String methodName=joinPoint.getSignature().getName();
            if(SecurityContextHolder.getContext().getAuthentication()!=null){
                performedBy=SecurityContextHolder.getContext().getAuthentication().getName();
            }
            Object[] args=joinPoint.getArgs();
            String targetId=args.length>0?String.valueOf(args[0]):"N/A";
            auditService.log(methodName,performedBy,targetId);
        }

}
