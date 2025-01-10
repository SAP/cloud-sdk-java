package com.sap.cloud.sdk.datamodel.odatav4.generator;
/*
  import javax.annotation.Nonnull;

  import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
  import com.sun.codemodel.JClassAlreadyExistsException;
  import com.sun.codemodel.JClassContainer;
  import com.sun.codemodel.JCodeModel;
  import com.sun.codemodel.JDefinedClass;
  import com.sun.codemodel.JExpr;
  import com.sun.codemodel.JFieldRef;
  import com.sun.codemodel.JMethod;
  import com.sun.codemodel.JMod;
  import com.sun.codemodel.JVar;

  import lombok.AccessLevel;
  import lombok.Getter;
  import lombok.RequiredArgsConstructor;

  @RequiredArgsConstructor
  class DefaultServiceBatchChangeSetGenerator
  {
   private static final String SERVICE_FIELD_NAME = "service";

   private final JCodeModel codeModel;

   ClassStub createDefaultImplementation(
       final JClassContainer targetPackage,
       final JDefinedClass basicServiceClass,
       final ServiceBatchChangeSetGenerator.InterfaceStub interfaceToImplement,
       final ServiceBatchGenerator.InterfaceStub serviceBatchInterface,
       final DefaultServiceBatchGenerator.ClassStub serviceBatchImplementation,
       final Service service )
       throws JClassAlreadyExistsException
   {
       final JDefinedClass changeSetInterface = interfaceToImplement.getServiceBatchChangeSetStub();
       final JDefinedClass batchInterface = serviceBatchInterface.getServiceBatchStub();
       final JDefinedClass batchImplementation = serviceBatchImplementation.getDefaultServiceBatchStub();

       final JDefinedClass defaultImplementation =
           createClassHeader(targetPackage, basicServiceClass, batchInterface, changeSetInterface);

       createBasicServiceField(defaultImplementation, basicServiceClass, service);
       createConstructor(defaultImplementation, batchImplementation, basicServiceClass, service);
       implementGetThisMethod(defaultImplementation);

       return new ClassStub(defaultImplementation);
   }

   private JDefinedClass createClassHeader(
       final JClassContainer targetPackage,
       final JDefinedClass basicServiceClass,
       final JDefinedClass batchInterface,
       final JDefinedClass changeSetInterface )
       throws JClassAlreadyExistsException
   {
       final JDefinedClass defaultImplementation =
           targetPackage._class(JMod.PUBLIC, createClassName(changeSetInterface));

       defaultImplementation
           ._extends(codeModel.ref(BatchChangeSetFluentHelperBasic.class).narrow(batchInterface, changeSetInterface));

       defaultImplementation._implements(changeSetInterface);

       defaultImplementation.javadoc().add(
           String.format(
               "Implementation of the {@link %s} interface, enabling you to combine multiple operations into one "
                   + "changeset. For further information have a look into the {@link %s %s}.",
               changeSetInterface.name(),
               basicServiceClass.fullName(),
               basicServiceClass.name()));

       return defaultImplementation;
   }

   private void createBasicServiceField(
       final JDefinedClass defaultImplementation,
       final JDefinedClass basicServiceClass,
       final Service service )
   {
       DeprecationUtils
           .createBasicServiceInterfaceField(defaultImplementation, basicServiceClass, SERVICE_FIELD_NAME, service);
   }

   private void createConstructor(
       final JDefinedClass defaultImplementation,
       final JDefinedClass serviceBatchImplementation,
       final JDefinedClass basicServiceInterface,
       final Service service )
   {
       final String fluentHelperParameterName = "batchFluentHelper";

       // in case of a deprecated API we need to reference the service classes (annotated as deprecate) via the
       // full path to prevent an import of a deprecated class which would result in a warning
       if( service.isDeprecated() ) {
           createConstructorForDeprecatedService(
               defaultImplementation,
               serviceBatchImplementation,
               basicServiceInterface,
               fluentHelperParameterName);
       } else {
           createConstructorForSupportedService(
               defaultImplementation,
               serviceBatchImplementation,
               basicServiceInterface,
               fluentHelperParameterName);
       }
   }

   private void createConstructorForSupportedService(
       final JDefinedClass defaultImplementation,
       final JDefinedClass serviceBatchImplementation,
       final JDefinedClass basicServiceInterface,
       final String fluentHelperParameterName )
   {
       final JMethod constructor = defaultImplementation.constructor(JMod.NONE);
       final JVar fluentHelperParam =
           constructor.param(JMod.FINAL, serviceBatchImplementation, fluentHelperParameterName);
       final JVar serviceParam = constructor.param(JMod.FINAL, basicServiceInterface, SERVICE_FIELD_NAME);
       serviceParam.annotate(Nonnull.class);
       fluentHelperParam.annotate(Nonnull.class);

       constructor.body().invoke("super").arg(fluentHelperParam).arg(fluentHelperParam);
       constructor.body().assign(JExpr.refthis(SERVICE_FIELD_NAME), serviceParam);
   }

   private void createConstructorForDeprecatedService(
       final JDefinedClass defaultImplementation,
       final JDefinedClass serviceBatchImplementation,
       final JDefinedClass basicServiceInterface,
       final String fluentHelperParameterName )
   {
       defaultImplementation.direct(
           "\n"
               + DeprecationUtils.INDENT
               + "@SuppressWarnings( \"deprecation\" )\n"
               + DeprecationUtils.INDENT
               + defaultImplementation.name()
               + "(\n"
               + DeprecationUtils.INDENT
               + DeprecationUtils.INDENT
               + "@Nonnull final "
               + serviceBatchImplementation.name()
               + " "
               + fluentHelperParameterName
               + ",\n"
               + DeprecationUtils.INDENT
               + DeprecationUtils.INDENT
               + "@Nonnull final "
               + basicServiceInterface.fullName()
               + " "
               + SERVICE_FIELD_NAME
               + " )\n"
               + DeprecationUtils.INDENT
               + "{\n"
               + DeprecationUtils.INDENT
               + DeprecationUtils.INDENT
               + "super("
               + fluentHelperParameterName
               + ", "
               + fluentHelperParameterName
               + ");\n"
               + DeprecationUtils.INDENT
               + DeprecationUtils.INDENT
               + "this."
               + SERVICE_FIELD_NAME
               + " = "
               + SERVICE_FIELD_NAME
               + ";\n"
               + DeprecationUtils.INDENT
               + "}");
   }

   private void implementGetThisMethod( final JDefinedClass defaultImplementation )
   {
       final JMethod createdMethod = defaultImplementation.method(JMod.PROTECTED, defaultImplementation, "getThis");
       createdMethod.body()._return(JExpr._this());

       createdMethod.annotate(Nonnull.class);
       createdMethod.annotate(Override.class);

       JavadocUtils.inheritJavadoc(createdMethod);
   }

   private void addCreateMethodImplementation(
       final JDefinedClass classToBeAdjusted,
       final JMethod basicServiceMethodToCall,
       final JMethod interfaceMethodToOverride )
   {
       addMethodImplementation(
           classToBeAdjusted,
           "addRequestCreate",
           basicServiceMethodToCall,
           interfaceMethodToOverride);
   }

   private void addUpdateMethodImplementation(
       final JDefinedClass classToBeAdjusted,
       final JMethod basicServiceMethodToCall,
       final JMethod interfaceMethodToOverride )
   {
       addMethodImplementation(
           classToBeAdjusted,
           "addRequestUpdate",
           basicServiceMethodToCall,
           interfaceMethodToOverride);
   }

   private void addDeleteMethodImplementation(
       final JDefinedClass classToBeAdjusted,
       final JMethod basicServiceMethodToCall,
       final JMethod interfaceMethodToOverride )
   {
       addMethodImplementation(
           classToBeAdjusted,
           "addRequestDelete",
           basicServiceMethodToCall,
           interfaceMethodToOverride);
   }

   private void addMethodImplementation(
       final JDefinedClass classToBeAdjusted,
       final String wrapperMethod,
       final JMethod basicServiceMethodToCall,
       final JMethod interfaceMethodToOverride )
   {
       final JMethod createdMethod =
           classToBeAdjusted.method(JMod.PUBLIC, interfaceMethodToOverride.type(), interfaceMethodToOverride.name());

       createdMethod.annotate(Nonnull.class);
       createdMethod.annotate(Override.class);

       if( interfaceMethodToOverride.params().size() != 1 ) {
           throw new ODataGeneratorException(
               "The list of parameter is expected to only contain the single entity to process, but it contained "
                   + interfaceMethodToOverride.params().size()
                   + " parameter.");
       }

       final JVar paramToCopy = interfaceMethodToOverride.params().get(0);
       final JVar entityParam =
           createdMethod.param(paramToCopy.mods().getValue(), paramToCopy.type(), paramToCopy.name());

       entityParam.annotate(Nonnull.class);

       createdMethod.body()._return(
           JExpr.invoke(wrapperMethod).arg(createServiceMethodReference(basicServiceMethodToCall)).arg(entityParam));

       JavadocUtils.inheritJavadoc(createdMethod);
   }

   private JFieldRef createServiceMethodReference( final JMethod basicServiceMethodToCall )
   {
       return JExpr.ref(SERVICE_FIELD_NAME + "::" + basicServiceMethodToCall.name());
   }

   private String createClassName( final JDefinedClass interfaceName )
   {
       return "Default" + interfaceName.name();
   }

   @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
   @Getter( AccessLevel.PACKAGE )
   final class ClassStub
   {
       private final JDefinedClass defaultBatchChangeSetStub;

       void addCreateMethodImplementation( final JMethod basicServiceMethod, final JMethod interfaceMethodToImplement )
       {
           DefaultServiceBatchChangeSetGenerator.this.addCreateMethodImplementation(
               defaultBatchChangeSetStub,
               basicServiceMethod,
               interfaceMethodToImplement);
       }

       void addUpdateMethodImplementation( final JMethod basicServiceMethod, final JMethod interfaceMethodToImplement )
       {
           DefaultServiceBatchChangeSetGenerator.this.addUpdateMethodImplementation(
               defaultBatchChangeSetStub,
               basicServiceMethod,
               interfaceMethodToImplement);
       }

       void addDeleteMethodImplementation( final JMethod basicServiceMethod, final JMethod interfaceMethodToImplement )
       {
           DefaultServiceBatchChangeSetGenerator.this.addDeleteMethodImplementation(
               defaultBatchChangeSetStub,
               basicServiceMethod,
               interfaceMethodToImplement);
       }
   }
  }
  */
