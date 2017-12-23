package es.udc.fi.dc.fd.signup;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.springframework.stereotype.Component;

import es.udc.fi.dc.fd.account.AccountRepository;

@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = es.udc.fi.dc.fd.signup.EmailExistsValidator.class)
@Documented
public @interface EmailExists {

	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

@Component
class EmailExistsValidator implements ConstraintValidator<es.udc.fi.dc.fd.signup.EmailExists, String> {

	private final AccountRepository accountRepository;

	public EmailExistsValidator(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public void initialize(es.udc.fi.dc.fd.signup.EmailExists constraintAnnotation) {
		// do nothing
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return !accountRepository.exists(value);
	}
}
