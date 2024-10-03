package br.com.ecommerce.common.annotations;

/**
 * Annotation representing a pair of user ID and role for use in tests.
 * This annotation is used to specify a combination of a user ID and a role
 * for test cases that require multiple user-role scenarios.
 * <p>
 * It is used in conjunction with annotation {@link TestCustomWithMockUser}
 * to provide test data for cases where a specific user ID and role combination
 * is needed to simulate different security contexts.
 * </p>
 */
public @interface IdRolePair {

    /**
     * The user ID to be associated with a specific test case.
     * 
     * @return the user ID
     */
    String id();

    /**
     * The role to be associated with the user for the test case.
     * 
     * @return the role of the user
     */
    String role();
}
