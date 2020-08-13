package com.ciandt.webl.dynamikos;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public final class MetaServer {
    private static final Logger LOGGER = Logger.getLogger("MetaServer");

    public List<Class<?>> searchControllers(final String packageName) throws IOException {
        LOGGER.info(String.format("Searching for controllers in path %s", packageName));

        Reflections reflections = new Reflections(packageName, 
            new SubTypesScanner(false));

        // Nós vamos usar apenas a utilidade getAllTypes para recuperar
        // Os tipos carregados no Classloader
        // Para o resto do processamento, usaremos a API de Reflections diretamente,
        // com objetivo didático.
        Set<String> knownTypes = reflections.getAllTypes();

        final List<Class<?>> controllerClasses = new ArrayList<Class<?>>();

        for(String knownType : knownTypes) {
            Class<?> packageClass = null;

            try {
                packageClass = Class.forName(knownType);
            } catch (final ClassNotFoundException cnf) {
                continue;
            }

            LOGGER.info(String.format("Found class %s in package %s. Checking for controller annotation...", packageClass.getName(),
                packageClass.getPackageName()));

            if (packageClass.isAnnotationPresent(Controller.class)) {
                LOGGER.info(String.format("Found controller %s in package %s", packageClass.getName(),
                        packageClass.getPackageName()));
                controllerClasses.add(packageClass);
            }
        }
        return controllerClasses;
    }

    public String getControllerPath(final Class<?> controllerClass) {
        final Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
        if (controllerAnnotation != null) {
            return controllerAnnotation.path();
        }
        return null;
    }

    public static void main(final String[] args) throws IOException {
        new MetaServer().searchControllers("com.ciandt.webl.dynamikos");
    }

    public HttpResponse invokeMethod(final Class<?> controllerClass, final HttpRequest request) {
        final Method[] methods = controllerClass.getDeclaredMethods();

        for (final Method method : methods) {
            final Operation op = method.getAnnotation(Operation.class);

            if (op != null && op.method().equals(request.getMethod().toLowerCase())) {
                try {
                    final Parameter[] methodParameters = method.getParameters();
                    final List<Object> parameters = new ArrayList<>();

                    LOGGER.info(String.format("Method %s, with %d parameters, will be called", method.getName(), methodParameters.length));

                    for(int i = 0; i < methodParameters.length; i++) {
                        final Parameter param = methodParameters[i];
                        final Param metaParam = param.getAnnotation(Param.class);

                        if(metaParam != null) {
                            final String reqParam = request.getParameter(metaParam.name());

                            LOGGER.info(String.format("Define method parameter %s=%s", metaParam.name(), reqParam));

                            parameters.add(reqParam);
                        }
                    }

                    final Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                    final HttpResponse response = (HttpResponse) method.invoke(controllerInstance, parameters.toArray());
                    
                    return response;
                }
                catch(NoSuchMethodException | SecurityException ne) {
                    LOGGER.warning("No default Constructor found in Controller");
                    return HttpResponse.INTERNAL_SERVER_ERROR;
                }
                catch(InstantiationException | InvocationTargetException | IllegalArgumentException | IllegalAccessException ex) {
                    LOGGER.log(Level.WARNING, String.format("Could not invocate method: %s", ex.getMessage()));
                    return HttpResponse.INTERNAL_SERVER_ERROR;
                }
            }
        }

        return HttpResponse.METHOD_NOT_ALLOWED;
	}
}
