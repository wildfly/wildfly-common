/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.common._private;

import java.security.Permission;
import java.security.PrivilegedActionException;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@MessageBundle(projectCode = "COM", length = 5)
public interface CommonMessages {
    CommonMessages msg = Messages.getBundle(CommonMessages.class);

    // simple validation

    @Message(id = 0, value = "Parameter '%s' may not be null")
    IllegalArgumentException nullParam(String paramName);

    @Message(id = 1, value = "Parameter '%s' must not be less than %d")
    IllegalArgumentException paramLessThan(String name, long min);

    @Message(id = 2, value = "Parameter '%s' must not be greater than than %d")
    IllegalArgumentException paramGreaterThan(String name, long max);

    @Message(id = 3, value = "Given offset of %d is greater than array length of %d")
    ArrayIndexOutOfBoundsException arrayOffsetGreaterThanLength(int offs, int arrayLength);

    @Message(id = 4, value = "Given offset of %d plus length of %d is greater than array length of %d")
    ArrayIndexOutOfBoundsException arrayOffsetLengthGreaterThanLength(int offs, int len, int arrayLength);

    @Message(id = 5, value = "Array index %d of parameter '%s' may not be null")
    IllegalArgumentException nullArrayParam(int index, String name);

    @Message(id = 6, value = "Parameter '%s' may not be null")
    NullPointerException nullParamNPE(String name);

    @Message(id = 7, value = "Invalid permission action '%s'")
    IllegalArgumentException invalidPermissionAction(String action);

    @Message(id = 8, value = "Parameter '%s' must not be empty")
    IllegalArgumentException emptyParam(String name);

    @Message(id = 9, value = "Invalid expression syntax at position %d")
    String invalidExpressionSyntax(int index);

    @Message(id = 10, value = "No environment property found named \"%s\"")
    IllegalArgumentException unresolvedEnvironmentProperty(String name);

    @Message(id = 11, value = "No system property found named \"%s\"")
    IllegalArgumentException unresolvedSystemProperty(String name);

    // execution path validation

    @Message(id = 100, value = "Method \"%s\" of class \"%s\" is not implemented")
    UnsupportedOperationException unsupported(String methodName, String className);

    // context classes

    @Message(id = 200, value = "Privileged action failed")
    PrivilegedActionException privilegedActionFailed(@Cause Exception e);

    // permissions

    @Message(id = 300, value = "Permission collection is read-only")
    SecurityException readOnlyPermissionCollection();

    @Message(id = 301, value = "Invalid permission type (expected %s, actual value was %s)")
    IllegalArgumentException invalidPermissionType(Class<? extends Permission> expectedType, Class<? extends Permission> actualType);

    // assertion errors

    @Message(id = 1000, value = "Internal error: Assertion failure: Unexpectedly null value")
    String unexpectedNullValue();

    @Message(id = 1001, value = "Internal error: Assertion failure: Current thread expected to hold lock for %s")
    String expectedLockHold(Object monitor);

    @Message(id = 1002, value = "Internal error: Assertion failure: Current thread expected to not hold lock for %s")
    String expectedLockNotHold(Object monitor);

    @Message(id = 1003, value = "Internal error: Assertion failure: Expected boolean value to be %s")
    String expectedBoolean(boolean expr);

    // internal state errors

    @Message(id = 2000, value = "Internal error: Unreachable code has been reached")
    IllegalStateException unreachableCode();

    @Message(id = 2001, value = "Internal error: Impossible switch condition encountered: %s")
    IllegalStateException impossibleSwitchCase(Object cond);

    // 3000-3099 reserved for reference queue logging (see {@link org.wildfly.common.ref.Log})

}
