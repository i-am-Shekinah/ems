package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.encentral.entities.User;
import play.libs.Json;
import play.mvc.*;
import services.AuthService;
import com.encentral.repositories.UserRepository;


import javax.inject.Inject;

// Use all modern OpenAPI 3.0 annotations
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.parameters.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.*;

import java.util.Optional;

@Tag(name = "Authentication", description = "Employee and Admin sign-in operations")
public class AuthController extends Controller {
    private final AuthService authService;
    private final UserRepository userRepo;

    @Inject
    public AuthController(AuthService authService, UserRepository userRepo) {
        this.authService = authService;
        this.userRepo = userRepo;
    }

    // 👇 The fix is here: using 'responseCode' (String) and documenting the Request Body
    @Operation(summary = "Sign in employee or admin",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Required credentials for sign-in",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"email\": \"user@example.com\", \"credential\": \"password/pin\"}")
                    )
            ),
            responses = {
                    // Use 'responseCode' (String)
                    @ApiResponse(responseCode = "200",
                            description = "Signed in successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"token\": \"jwt-token-string\", \"role\": \"EMPLOYEE\", \"email\": \"user@example.com\"}")
                            )
                    ),
                    // Use 'responseCode' (String)
                    @ApiResponse(responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"Authentication failed: Invalid credentials\"}")
                            )
                    )
            })
    public Result signIn(Http.Request request) {
        ObjectNode body = (ObjectNode) request.body().asJson();
        if (body == null) return badRequest("json required");

        // Safety checks for missing keys, which is good practice
        if (!body.has("email") || !body.has("credential")) {
            return badRequest("Missing 'email' or 'credential' in request body.");
        }

        String email = body.get("email").asText();
        String credential = body.get("credential").asText(); // pin or password

        try {
            User u = authService.signInEmployee(email, credential);
            ObjectNode out = Json.newObject();
            out.put("token", u.token);
            out.put("role", u.role.name());
            out.put("email", u.email);
            return ok(out);
        } catch (RuntimeException ex) {
            // The method returns unauthorized() (HTTP 401), matching the documentation
            return unauthorized(Json.newObject().put("error", ex.getMessage()));
        }
    }
}