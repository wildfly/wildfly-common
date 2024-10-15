module org.wildfly.common {
    requires java.compiler;
    requires java.xml;
    requires java.transaction.xa;
    requires java.sql;

    requires io.smallrye.common.cpu;
    requires io.smallrye.common.expression;
    requires io.smallrye.common.net;
    requires io.smallrye.common.os;
    requires io.smallrye.common.ref;

    requires org.jboss.logging;

    requires static io.smallrye.common.function;

    requires static org.jboss.logging.annotations;
}