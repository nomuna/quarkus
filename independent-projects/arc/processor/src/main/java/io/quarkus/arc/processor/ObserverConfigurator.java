package io.quarkus.arc.processor;

import io.quarkus.gizmo.MethodCreator;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.ObserverMethod;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;
import org.jboss.jandex.Type.Kind;

/**
 * This construct is not thread-safe.
 */
public final class ObserverConfigurator implements Consumer<AnnotationInstance> {

    final Consumer<ObserverConfigurator> consumer;

    final DotName beanClass;

    Type observedType;

    final Set<AnnotationInstance> observedQualifiers;

    int priority;

    boolean isAsync;

    TransactionPhase transactionPhase;

    Consumer<MethodCreator> notifyConsumer;

    public ObserverConfigurator(DotName beanClass, Consumer<ObserverConfigurator> consumer) {
        this.beanClass = beanClass;
        this.consumer = consumer;
        this.observedQualifiers = new HashSet<>();
        this.priority = ObserverMethod.DEFAULT_PRIORITY;
        this.isAsync = false;
        this.transactionPhase = TransactionPhase.IN_PROGRESS;
    }

    public ObserverConfigurator observedType(Class<?> observedType) {
        this.observedType = Type.create(DotName.createSimple(observedType.getName()), Kind.CLASS);
        return this;
    }

    public ObserverConfigurator observedType(Type observedType) {
        this.observedType = observedType;
        return this;
    }

    public ObserverConfigurator addQualifier(Class<? extends Annotation> annotationClass) {
        return addQualifier(DotName.createSimple(annotationClass.getName()));
    }

    public ObserverConfigurator addQualifier(DotName annotationName) {
        return addQualifier(AnnotationInstance.create(annotationName, null, new AnnotationValue[] {}));
    }

    public ObserverConfigurator addQualifier(AnnotationInstance qualifier) {
        this.observedQualifiers.add(qualifier);
        return this;
    }

    public QualifierConfigurator<ObserverConfigurator> addQualifier() {
        return new QualifierConfigurator<>(this);
    }

    public ObserverConfigurator priority(int priority) {
        this.priority = priority;
        return this;
    }

    public ObserverConfigurator async(boolean value) {
        this.isAsync = value;
        return this;
    }

    public ObserverConfigurator transactionPhase(TransactionPhase transactionPhase) {
        this.transactionPhase = transactionPhase;
        return this;
    }

    public ObserverConfigurator notify(Consumer<MethodCreator> notifyConsumer) {
        this.notifyConsumer = notifyConsumer;
        return this;
    }

    public void done() {
        consumer.accept(this);
    }

    @Override
    public void accept(AnnotationInstance qualifier) {
        addQualifier(qualifier);
    }

}
