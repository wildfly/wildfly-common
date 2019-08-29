package org.wildfly.common.ref;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@MessageLogger(projectCode = "COM", length = 5)
interface Log {
    Log log = Logger.getMessageLogger(Log.class, "org.wildfly.common.ref");

    // 3000-3099 reserved for reference queue logging

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 3000, value = "Reaping a reference failed")
    void reapFailed(@Cause Throwable cause);

}
