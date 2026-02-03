# Upgrade Progress

  ### ✅ Generate Upgrade Plan
  - [[View Log]](logs\1.generatePlan.log)

  ### ✅ Confirm Upgrade Plan
  - [[View Log]](logs\2.confirmPlan.log)

  ### ❗ Setup Development Environment
  - [[View Log]](logs\3.setupEnvironment.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  #### Errors
  - Project compile failed with 1 errors. The project must be compileable before upgrading it, please fix the errors first and then invoke tool #setup\_upgrade\_environment again to setup development environment: - === Config File error     The below errors can be due to missing dependencies. You may have to refer     to the config files provided earlier to solve it.     'errorMessage': Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project distributed-rate-limiter: Compilation failure: Compilation failure:   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/executor/RedisRateLimitExecutor.java:[267,26] reference to execute is ambiguous     both method \\<T\\>execute(org.springframework.data.redis.core.RedisCallback\\<T\\>) in org.springframework.data.redis.core.RedisTemplate and method \\<T\\>execute(org.springframework.data.redis.core.SessionCallback\\<T\\>) in org.springframework.data.redis.core.RedisTemplate match   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/filter/RateLimiterFilter.java:[191,47] cannot find symbol     symbol:   variable SC\\_TOO\\_MANY\\_REQUESTS     location: interface jakarta.servlet.http.HttpServletResponse    \`\`\`   Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project distributed-rate-limiter: Compilation failure: Compilation failure:   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/executor/RedisRateLimitExecutor.java:[267,26] reference to execute is ambiguous     both method \\<T\\>execute(org.springframework.data.redis.core.RedisCallback\\<T\\>) in org.springframework.data.redis.core.RedisTemplate and method \\<T\\>execute(org.springframework.data.redis.core.SessionCallback\\<T\\>) in org.springframework.data.redis.core.RedisTemplate match   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/filter/RateLimiterFilter.java:[191,47] cannot find symbol     symbol:   variable SC\\_TOO\\_MANY\\_REQUESTS     location: interface jakarta.servlet.http.HttpServletResponse   \`\`\`
  </details>

  ### ✅ PreCheck
  - [[View Log]](logs\4.precheck.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ❗ Precheck - Build project
    - [[View Log]](logs\4.1.precheck-buildProject.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Command
    `mvn clean test-compile -q -B -fn`
    
    #### Errors
    - === Config File error     The below errors can be due to missing dependencies. You may have to refer     to the config files provided earlier to solve it.     'errorMessage': Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project distributed-rate-limiter: Compilation failure: Compilation failure:   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/executor/RedisRateLimitExecutor.java:[267,26] reference to execute is ambiguous     both method \<T\>execute(org.springframework.data.redis.core.RedisCallback\<T\>) in org.springframework.data.redis.core.RedisTemplate and method \<T\>execute(org.springframework.data.redis.core.SessionCallback\<T\>) in org.springframework.data.redis.core.RedisTemplate match   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/filter/RateLimiterFilter.java:[191,47] cannot find symbol     symbol:   variable SC\_TOO\_MANY\_REQUESTS     location: interface jakarta.servlet.http.HttpServletResponse 
      ```
      Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project distributed-rate-limiter: Compilation failure: Compilation failure:   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/executor/RedisRateLimitExecutor.java:[267,26] reference to execute is ambiguous     both method \<T\>execute(org.springframework.data.redis.core.RedisCallback\<T\>) in org.springframework.data.redis.core.RedisTemplate and method \<T\>execute(org.springframework.data.redis.core.SessionCallback\<T\>) in org.springframework.data.redis.core.RedisTemplate match   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/filter/RateLimiterFilter.java:[191,47] cannot find symbol     symbol:   variable SC\_TOO\_MANY\_REQUESTS     location: interface jakarta.servlet.http.HttpServletResponse
      ```
    </details>
  </details>

  ### ❗ Setup Development Environment
  - [[View Log]](logs\5.setupEnvironment.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  #### Errors
  - Project compile failed with 1 errors. The project must be compileable before upgrading it, please fix the errors first and then invoke tool #setup\_upgrade\_environment again to setup development environment: - === Config File error     The below errors can be due to missing dependencies. You may have to refer     to the config files provided earlier to solve it.     'errorMessage': Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project distributed-rate-limiter: Compilation failure   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/executor/RedisRateLimitExecutor.java:[267,36] cannot find symbol     symbol:   class RedisCallback     location: class com.company.ratelimiter.executor.RedisRateLimitExecutor    \`\`\`   Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project distributed-rate-limiter: Compilation failure   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/executor/RedisRateLimitExecutor.java:[267,36] cannot find symbol     symbol:   class RedisCallback     location: class com.company.ratelimiter.executor.RedisRateLimitExecutor   \`\`\`
  </details>

  ### ✅ PreCheck
  - [[View Log]](logs\6.precheck.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ❗ Precheck - Build project
    - [[View Log]](logs\6.1.precheck-buildProject.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Command
    `mvn clean test-compile -q -B -fn`
    
    #### Errors
    - === Config File error     The below errors can be due to missing dependencies. You may have to refer     to the config files provided earlier to solve it.     'errorMessage': Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project distributed-rate-limiter: Compilation failure   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/executor/RedisRateLimitExecutor.java:[267,36] cannot find symbol     symbol:   class RedisCallback     location: class com.company.ratelimiter.executor.RedisRateLimitExecutor 
      ```
      Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project distributed-rate-limiter: Compilation failure   /c:/Users/Ravid r/distributed-rate-limiter/src/main/java/com/company/ratelimiter/executor/RedisRateLimitExecutor.java:[267,36] cannot find symbol     symbol:   class RedisCallback     location: class com.company.ratelimiter.executor.RedisRateLimitExecutor
      ```
    </details>
  </details>

  ### ✅ Setup Development Environment
  - [[View Log]](logs\7.setupEnvironment.log)

  ### ✅ PreCheck
  - [[View Log]](logs\8.precheck.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ✅ Precheck - Build project
    - [[View Log]](logs\8.1.precheck-buildProject.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Command
    `mvn clean test-compile -q -B -fn`
    </details>
  
    ### ✅ Precheck - Validate CVEs
    - [[View Log]](logs\8.2.precheck-validateCves.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### CVE issues
    </details>
  
    ### ❗ Precheck - Run tests
    - [[View Log]](logs\8.3.precheck-runTests.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Errors
    - Failed to load ApplicationContext for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      ```
      java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:180)
      	at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
      	at org.springframework.test.context.web.ServletTestExecutionListener.setUpRequestContextIfNecessary(ServletTestExecutionListener.java:191)
      	at org.springframework.test.context.web.ServletTestExecutionListener.prepareTestInstance(ServletTestExecutionListener.java:130)
      	at org.springframework.test.context.TestContextManager.prepareTestInstance(TestContextManager.java:260)
      	at org.springframework.test.context.junit.jupiter.SpringExtension.postProcessTestInstance(SpringExtension.java:163)
      	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
      	at java.base/java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:179)
      	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
      	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
      	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
      	at java.base/java.util.stream.StreamSpliterators$WrappingSpliterator.forEachRemaining(StreamSpliterators.java:310)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:735)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:734)
      	at java.base/java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:762)
      	at java.base/java.util.Optional.orElseGet(Optional.java:364)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'rateLimiterService' defined in file [C:\Users\Ravid r\distributed-rate-limiter\target\classes\com\company\ratelimiter\core\RateLimiterService.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'redisRateLimitExecutor' defined in file [C:\Users\Ravid r\distributed-rate-limiter\target\classes\com\company\ratelimiter\executor\RedisRateLimitExecutor.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'redisTemplate' defined in class path resource [com/company/ratelimiter/config/RedisConfiguration.class]: Unsatisfied dependency expressed through method 'redisTemplate' parameter 0: Error creating bean with name 'redisConnectionFactory' defined in class path resource [com/company/ratelimiter/config/RedisConfiguration.class]: Failed to instantiate [org.springframework.data.redis.connection.RedisConnectionFactory]: Factory method 'redisConnectionFactory' threw exception with message: org/apache/commons/pool2/impl/GenericObjectPoolConfig
      	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:802)
      	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:241)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1354)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1191)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325)
      	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)
      	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:975)
      	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:960)
      	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:625)
      	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:762)
      	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:464)
      	at org.springframework.boot.SpringApplication.run(SpringApplication.java:334)
      	at org.springframework.boot.test.context.SpringBootContextLoader.lambda$loadContext$3(SpringBootContextLoader.java:137)
      	at org.springframework.util.function.ThrowingSupplier.get(ThrowingSupplier.java:58)
      	at org.springframework.util.function.ThrowingSupplier.get(ThrowingSupplier.java:46)
      	at org.springframework.boot.SpringApplication.withHook(SpringApplication.java:1458)
      	at org.springframework.boot.test.context.SpringBootContextLoader$ContextLoaderHook.run(SpringBootContextLoader.java:552)
      	at org.springframework.boot.test.context.SpringBootContextLoader.loadContext(SpringBootContextLoader.java:137)
      	at org.springframework.boot.test.context.SpringBootContextLoader.loadContext(SpringBootContextLoader.java:108)
      	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:225)
      	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:152)
      	... 17 more
      Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'redisRateLimitExecutor' defined in file [C:\Users\Ravid r\distributed-rate-limiter\target\classes\com\company\ratelimiter\executor\RedisRateLimitExecutor.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'redisTemplate' defined in class path resource [com/company/ratelimiter/config/RedisConfiguration.class]: Unsatisfied dependency expressed through method 'redisTemplate' parameter 0: Error creating bean with name 'redisConnectionFactory' defined in class path resource [com/company/ratelimiter/config/RedisConfiguration.class]: Failed to instantiate [org.springframework.data.redis.connection.RedisConnectionFactory]: Factory method 'redisConnectionFactory' threw exception with message: org/apache/commons/pool2/impl/GenericObjectPoolConfig
      	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:802)
      	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:241)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1354)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1191)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325)
      	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)
      	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254)
      	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443)
      	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353)
      	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:911)
      	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:789)
      	... 41 more
      Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'redisTemplate' defined in class path resource [com/company/ratelimiter/config/RedisConfiguration.class]: Unsatisfied dependency expressed through method 'redisTemplate' parameter 0: Error creating bean with name 'redisConnectionFactory' defined in class path resource [com/company/ratelimiter/config/RedisConfiguration.class]: Failed to instantiate [org.springframework.data.redis.connection.RedisConnectionFactory]: Factory method 'redisConnectionFactory' threw exception with message: org/apache/commons/pool2/impl/GenericObjectPoolConfig
      	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:802)
      	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:546)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1164)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325)
      	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)
      	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254)
      	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443)
      	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353)
      	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:911)
      	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:789)
      	... 55 more
      Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'redisConnectionFactory' defined in class path resource [com/company/ratelimiter/config/RedisConfiguration.class]: Failed to instantiate [org.springframework.data.redis.connection.RedisConnectionFactory]: Factory method 'redisConnectionFactory' threw exception with message: org/apache/commons/pool2/impl/GenericObjectPoolConfig
      	at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:655)
      	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:489)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1164)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561)
      	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325)
      	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323)
      	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)
      	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254)
      	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443)
      	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353)
      	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:911)
      	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:789)
      	... 69 more
      Caused by: org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.data.redis.connection.RedisConnectionFactory]: Factory method 'redisConnectionFactory' threw exception with message: org/apache/commons/pool2/impl/GenericObjectPoolConfig
      	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:177)
      	at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:651)
      	... 83 more
      Caused by: java.lang.NoClassDefFoundError: org/apache/commons/pool2/impl/GenericObjectPoolConfig
      	at org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration$LettucePoolingClientConfigurationBuilder.\<init\>(LettucePoolingClientConfiguration.java:94)
      	at org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration.builder(LettucePoolingClientConfiguration.java:51)
      	at com.company.ratelimiter.config.RedisConfiguration.redisConnectionFactory(RedisConfiguration.java:59)
      	at com.company.ratelimiter.config.RedisConfiguration$$SpringCGLIB$$0.CGLIB$redisConnectionFactory$1(\<generated\>)
      	at com.company.ratelimiter.config.RedisConfiguration$$SpringCGLIB$$FastClass$$1.invoke(\<generated\>)
      	at org.springframework.cglib.proxy.MethodProxy.invokeSuper(MethodProxy.java:258)
      	at org.springframework.context.annotation.ConfigurationClassEnhancer$BeanMethodInterceptor.intercept(ConfigurationClassEnhancer.java:331)
      	at com.company.ratelimiter.config.RedisConfiguration$$SpringCGLIB$$0.redisConnectionFactory(\<generated\>)
      	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
      	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:140)
      	... 84 more
      Caused by: java.lang.ClassNotFoundException: org.apache.commons.pool2.impl.GenericObjectPoolConfig
      	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
      	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
      	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:525)
      	... 94 more
      
      ```
    - ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      ```
      java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
      	at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
      	at org.springframework.test.context.web.ServletTestExecutionListener.setUpRequestContextIfNecessary(ServletTestExecutionListener.java:191)
      	at org.springframework.test.context.web.ServletTestExecutionListener.prepareTestInstance(ServletTestExecutionListener.java:130)
      	at org.springframework.test.context.TestContextManager.prepareTestInstance(TestContextManager.java:260)
      	at org.springframework.test.context.junit.jupiter.SpringExtension.postProcessTestInstance(SpringExtension.java:163)
      	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
      	at java.base/java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:179)
      	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
      	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
      	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
      	at java.base/java.util.stream.StreamSpliterators$WrappingSpliterator.forEachRemaining(StreamSpliterators.java:310)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:735)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:734)
      	at java.base/java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:762)
      	at java.base/java.util.Optional.orElseGet(Optional.java:364)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      
      ```
    - ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      ```
      java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
      	at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
      	at org.springframework.test.context.web.ServletTestExecutionListener.setUpRequestContextIfNecessary(ServletTestExecutionListener.java:191)
      	at org.springframework.test.context.web.ServletTestExecutionListener.prepareTestInstance(ServletTestExecutionListener.java:130)
      	at org.springframework.test.context.TestContextManager.prepareTestInstance(TestContextManager.java:260)
      	at org.springframework.test.context.junit.jupiter.SpringExtension.postProcessTestInstance(SpringExtension.java:163)
      	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
      	at java.base/java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:179)
      	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
      	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
      	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
      	at java.base/java.util.stream.StreamSpliterators$WrappingSpliterator.forEachRemaining(StreamSpliterators.java:310)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:735)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:734)
      	at java.base/java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:762)
      	at java.base/java.util.Optional.orElseGet(Optional.java:364)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      
      ```
    - ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      ```
      java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
      	at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
      	at org.springframework.test.context.web.ServletTestExecutionListener.setUpRequestContextIfNecessary(ServletTestExecutionListener.java:191)
      	at org.springframework.test.context.web.ServletTestExecutionListener.prepareTestInstance(ServletTestExecutionListener.java:130)
      	at org.springframework.test.context.TestContextManager.prepareTestInstance(TestContextManager.java:260)
      	at org.springframework.test.context.junit.jupiter.SpringExtension.postProcessTestInstance(SpringExtension.java:163)
      	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
      	at java.base/java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:179)
      	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
      	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
      	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
      	at java.base/java.util.stream.StreamSpliterators$WrappingSpliterator.forEachRemaining(StreamSpliterators.java:310)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:735)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:734)
      	at java.base/java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:762)
      	at java.base/java.util.Optional.orElseGet(Optional.java:364)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      
      ```
    - ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      ```
      java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@143fefaf testClass = com.company.ratelimiter.integration.RateLimiterIntegrationTest, locations = [], classes = [com.company.ratelimiter.RateLimiterApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [PropertySourceDescriptor[locations=[], ignoreResourceNotFound=false, name=null, propertySourceFactory=null, encoding=null]], propertySourceProperties = ["ratelimiter.enabled=true", "ratelimiter.redis.host=localhost", "ratelimiter.redis.port=6379", "spring.redis.host=localhost", "spring.redis.port=6379", "org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@642a7222, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@147e2ae7, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@64b0598, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@34129c78, org.springframework.boot.test.context.SpringBootTestAnnotation@a45f9ddd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
      	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
      	at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
      	at org.springframework.test.context.web.ServletTestExecutionListener.setUpRequestContextIfNecessary(ServletTestExecutionListener.java:191)
      	at org.springframework.test.context.web.ServletTestExecutionListener.prepareTestInstance(ServletTestExecutionListener.java:130)
      	at org.springframework.test.context.TestContextManager.prepareTestInstance(TestContextManager.java:260)
      	at org.springframework.test.context.junit.jupiter.SpringExtension.postProcessTestInstance(SpringExtension.java:163)
      	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
      	at java.base/java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:179)
      	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
      	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
      	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
      	at java.base/java.util.stream.StreamSpliterators$WrappingSpliterator.forEachRemaining(StreamSpliterators.java:310)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:735)
      	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:734)
      	at java.base/java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:762)
      	at java.base/java.util.Optional.orElseGet(Optional.java:364)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
      
      ```
    
    #### Test result
    | Total | Passed | Failed | Skipped | Errors |
    |-------|--------|--------|---------|--------|
    | 10 | 4 | 1 | 0 | 5 |
    </details>
  </details>

  ### ⏳ Upgrade project to use `Java 21` ...Running
  
  
  - ###
    ### ⏳ Upgrade using Agent ...Running