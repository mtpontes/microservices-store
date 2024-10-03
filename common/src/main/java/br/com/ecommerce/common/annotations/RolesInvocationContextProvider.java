package br.com.ecommerce.common.annotations;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.ecommerce.common.user.UserDetailsImpl;

/**
 * Invocation context provider for testing with multiple user roles.
 * Implements the {@link TestTemplateInvocationContextProvider} interface, allowing
 * a test to be executed for different combinations of user IDs and roles.
 * <p>
 * The test is configured according to the {@link TestCustomWithMockUser} annotation, which defines
 * user roles, IDs, or ID-role pairs. This provider ensures that for
 * each configuration, a new context is created and authentication is adjusted
 * according to the given user and role.
 * </p>
 */
public class RolesInvocationContextProvider implements TestTemplateInvocationContextProvider {

    /**
     * Checks whether the test method supports the {@link TestCustomWithMockUser} annotation.
     * 
     * @param context the current test extension context
     * @return {@code true} if the method is annotated with {@link TestCustomWithMockUser},
     *         otherwise {@code false}
     */
    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return context.getTestMethod()
            .map(method -> method.isAnnotationPresent(TestCustomWithMockUser.class))
            .orElse(false);
    }

    /**
     * Provides the invocation contexts for the test method based on the parameters
     * defined in the {@link TestCustomWithMockUser} annotation. If only roles are provided, 
     * contexts will be created based on the role. If only user IDs are provided,
     * contexts will be created based on the user ID. If ID and role pairs are provided, the 
     * context will be configured for each combination.
     * 
     * @param context the current test extension context
     * @return a {@code Stream} of {@link TestTemplateInvocationContext} configured
     *         according to the parameters of the {@link TestCustomWithMockUser} annotation
     */
    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        var rolesAnnotation = context.getRequiredTestMethod().getAnnotation(TestCustomWithMockUser.class);

        if (rolesAnnotation.roles().length > 0 && rolesAnnotation.userId().length == 0)
            return Stream.of(rolesAnnotation.roles())
                .map(role -> new GenericTestTemplateInvocationContext(context, null, role));

        if (rolesAnnotation.userId().length > 0 && rolesAnnotation.roles().length == 0) 
            return Stream.of(rolesAnnotation.userId())
                .map(userId -> new GenericTestTemplateInvocationContext(context, userId, null));

        if (rolesAnnotation.idRolePair().length > 0) 
            return Stream.of(rolesAnnotation.idRolePair())
                .map(pair -> new GenericTestTemplateInvocationContext(context, pair.id(), pair.role()));

        return Stream.empty();
    }

    /**
     * Generic inner class that implements the test template invocation context.
     * Configures the test name and authentication data based on the provided ID and role.
     */
    static class GenericTestTemplateInvocationContext implements TestTemplateInvocationContext {
        private final ExtensionContext context;
        private final String id;
        private final String role;

        /**
         * Creates a new test invocation context.
         * 
         * @param context the current test extension context
         * @param id the user ID to be used for the test, or {@code null} if not applicable
         * @param role the user role to be used for the test, or {@code null} if not applicable
         */
        GenericTestTemplateInvocationContext(ExtensionContext context, String id, String role) {
            this.context = context;
            this.id = id;
            this.role = role;
        }

        /**
         * Returns the name to be displayed in the test execution report, formatted
         * according to the ID and role combination.
         * 
         * @param invocationIndex the index of the invocation
         * @return the formatted name of the test method along with the role and/or ID
         */
        @Override
        public String getDisplayName(int invocationIndex) {
            String methodName = context.getRequiredTestMethod().getName();
            if (role != null && id == null) return String.format("%s - role=%s", methodName, role);
            if (id != null && role == null) return String.format("%s - userId=%s", methodName, id);
            if (id != null && role != null) return String.format("%s - userId=%s, role=%s", methodName, id, role);
            return methodName;
        }

        /**
         * Returns a list of additional extensions for configuring the execution environment
         * of the test. In this case, it configures the security context based on the ID and role.
         * 
         * @return a list of extensions for configuring user authentication
         */
        @Override
        public List<Extension> getAdditionalExtensions() {
            return List.of(new BeforeEachCallback() {

                /**
                 * Configures the security context before each test run,
                 * assigning the user based on ID and/or role.
                 * 
                 * @param context the current test extension context
                 */
                @Override
                public void beforeEach(ExtensionContext context) {
                    var user = new UserDetailsImpl(id, null, role);
                    SecurityContextHolder.getContext()
                        .setAuthentication(
                            new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
                }
            });
        }
    }
}