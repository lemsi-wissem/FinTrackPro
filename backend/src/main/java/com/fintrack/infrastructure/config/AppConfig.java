package com.fintrack.infrastructure.config;

import com.fintrack.application.usecase.*;
import com.fintrack.domain.port.in.*;
import com.fintrack.domain.port.out.*;
import com.fintrack.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    @Value("${app.password-reset.token-ttl-seconds:3600}")
    private long passwordResetTokenTtlSeconds;

    /**
     * Prevents Spring Boot from auto-registering JwtAuthenticationFilter as a
     * standalone servlet filter. It is already manually added to the Spring Security
     * filter chain via addFilterBefore() in SecurityConfig, so we only want it there.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepositoryPort userRepo,
                                                    PasswordEncoderPort encoder,
                                                    EmailSenderPort emailSender,
                                                    TokenProviderPort tokenProvider) {
        return new RegisterUserUseCaseImpl(userRepo, encoder, emailSender, tokenProvider);
    }

    @Bean
    public VerifyEmailUseCase verifyEmailUseCase(UserRepositoryPort userRepo,
                                                  TokenProviderPort tokenProvider) {
        return new VerifyEmailUseCaseImpl(userRepo, tokenProvider);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(UserRepositoryPort userRepo,
                                              PasswordEncoderPort encoder,
                                              TokenProviderPort tokenProvider,
                                              RefreshTokenRepositoryPort refreshTokenRepo) {
        return new LoginUserUseCaseImpl(userRepo, encoder, tokenProvider, refreshTokenRepo, refreshTokenExpirationMs);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(RefreshTokenRepositoryPort refreshTokenRepo,
                                                    UserRepositoryPort userRepo,
                                                    TokenProviderPort tokenProvider) {
        return new RefreshTokenUseCaseImpl(refreshTokenRepo, userRepo, tokenProvider, refreshTokenExpirationMs);
    }

    @Bean
    public LogoutUserUseCase logoutUserUseCase(RefreshTokenRepositoryPort refreshTokenRepo,
                                                TokenBlacklistPort blacklist,
                                                TokenProviderPort tokenProvider) {
        return new LogoutUserUseCaseImpl(refreshTokenRepo, blacklist, tokenProvider);
    }

    @Bean
    public GetCurrentUserUseCase getCurrentUserUseCase(UserRepositoryPort userRepo) {
        return new GetCurrentUserUseCaseImpl(userRepo);
    }

    @Bean
    public ForgotPasswordUseCase forgotPasswordUseCase(UserRepositoryPort userRepo,
                                                        PasswordResetTokenPort resetTokenPort,
                                                        EmailSenderPort emailSender) {
        return new ForgotPasswordUseCaseImpl(userRepo, resetTokenPort, emailSender, passwordResetTokenTtlSeconds);
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(PasswordResetTokenPort resetTokenPort,
                                                      UserRepositoryPort userRepo,
                                                      PasswordEncoderPort encoder) {
        return new ResetPasswordUseCaseImpl(resetTokenPort, userRepo, encoder);
    }

    // Category use cases
    @Bean
    public GetCategoriesUseCase getCategoriesUseCase(CategoryRepositoryPort categoryRepo) {
        return new GetCategoriesUseCaseImpl(categoryRepo);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(CategoryRepositoryPort categoryRepo) {
        return new CreateCategoryUseCaseImpl(categoryRepo);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryRepositoryPort categoryRepo) {
        return new UpdateCategoryUseCaseImpl(categoryRepo);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(CategoryRepositoryPort categoryRepo) {
        return new DeleteCategoryUseCaseImpl(categoryRepo);
    }

    // Transaction use cases
    @Bean
    public CreateTransactionUseCase createTransactionUseCase(TransactionRepositoryPort transactionRepo) {
        return new CreateTransactionUseCaseImpl(transactionRepo);
    }

    @Bean
    public GetTransactionsUseCase getTransactionsUseCase(TransactionRepositoryPort transactionRepo) {
        return new GetTransactionsUseCaseImpl(transactionRepo);
    }

    @Bean
    public UpdateTransactionUseCase updateTransactionUseCase(TransactionRepositoryPort transactionRepo) {
        return new UpdateTransactionUseCaseImpl(transactionRepo);
    }

    @Bean
    public DeleteTransactionUseCase deleteTransactionUseCase(TransactionRepositoryPort transactionRepo) {
        return new DeleteTransactionUseCaseImpl(transactionRepo);
    }

    // Budget use cases
    @Bean
    public UpsertBudgetUseCase upsertBudgetUseCase(BudgetRepositoryPort budgetRepo) {
        return new UpsertBudgetUseCaseImpl(budgetRepo);
    }

    @Bean
    public GetBudgetsUseCase getBudgetsUseCase(BudgetRepositoryPort budgetRepo,
                                                TransactionRepositoryPort transactionRepo,
                                                CategoryRepositoryPort categoryRepo) {
        return new GetBudgetsUseCaseImpl(budgetRepo, transactionRepo, categoryRepo);
    }

    @Bean
    public DeleteBudgetUseCase deleteBudgetUseCase(BudgetRepositoryPort budgetRepo) {
        return new DeleteBudgetUseCaseImpl(budgetRepo);
    }

    // Dashboard use case
    @Bean
    public GetDashboardSummaryUseCase getDashboardSummaryUseCase(TransactionRepositoryPort transactionRepo,
                                                                   CategoryRepositoryPort categoryRepo) {
        return new GetDashboardSummaryUseCaseImpl(transactionRepo, categoryRepo);
    }

    // Notification use cases
    @Bean
    public GetNotificationsUseCase getNotificationsUseCase(NotificationRepositoryPort notificationRepo) {
        return new GetNotificationsUseCaseImpl(notificationRepo);
    }

    @Bean
    public MarkNotificationsReadUseCase markNotificationsReadUseCase(NotificationRepositoryPort notificationRepo) {
        return new MarkNotificationsReadUseCaseImpl(notificationRepo);
    }

    // Import use case
    @Bean
    public ImportTransactionsUseCase importTransactionsUseCase(TransactionRepositoryPort transactionRepo,
                                                                CategoryRepositoryPort categoryRepo) {
        return new ImportTransactionsUseCaseImpl(transactionRepo, categoryRepo);
    }
}
