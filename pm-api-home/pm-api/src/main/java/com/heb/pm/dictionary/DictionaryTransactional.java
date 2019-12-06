package com.heb.pm.dictionary;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Since the application has multiple transaction managers, this is a convenience annotation that allows you to
 * annotate a method as transactional using the Dictionary transaction manager.
 *
 * @author m314029
 * @since 1.2.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional(transactionManager = "dictionaryTransactionManager")
public @interface DictionaryTransactional {
}
