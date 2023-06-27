package elfak.mosis.tourguide.domain.helper

import androidx.core.text.isDigitsOnly
import elfak.mosis.tourguide.domain.models.tour.TourConstants
import elfak.mosis.tourguide.domain.models.validation.LoginCredentials
import elfak.mosis.tourguide.domain.models.validation.UserCredentials
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidationHelper @Inject constructor() {
    // Validate objects, throws exception if validation fails.
    // Catch them properly

    val emailRegex = Regex("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$")
    val charsOnly = Regex("^[\\p{L}\\s]+$")

    fun validateLoginCredentials(credentials: LoginCredentials) {
        if (credentials.email.isBlank()) {
            throw Exception("Email cannot be empty")
        }
        if (!credentials.email.matches(emailRegex)) {
            throw Exception("Email not valid. Proper form: 'tour@tourguide.com'")
        }
        if (credentials.password.isBlank()) {
            throw Exception("Password cannot be empty")
        }
    }

    fun validateEmailAdress(email: String){
        if (email.isBlank()) {
            throw Exception("Email cannot be empty")
        }
        if (!email.matches(emailRegex)) {
            throw Exception("Email not valid. Proper form: 'tour@tourguide.com'")
        }
    }

    fun validateUserCredentials(credentials: UserCredentials) {
        // fullname
        if (credentials.fullname.isBlank()) {
            throw Exception("Fullname cannot be empty.")
        }
        if (!credentials.fullname.matches(charsOnly)) {
            throw Exception("Fullname must be only characters. Or characters are not ASCII only")
        }

        // username
        if (credentials.username.isBlank()) {
            throw Exception("Username cannot be empty.")
        }

        // phone number
        if (credentials.phoneNumber.isNotBlank()) {
            if (!credentials.phoneNumber.isDigitsOnly()) {
                throw Exception("Phone number must be only digits")
            }
        }

        // email
        if (credentials.email.isBlank() ) {
            throw Exception("Email cannot be empty.")
        }
        if (!credentials.email.matches(emailRegex)) {
            throw Exception("Email not valid. Proper form: 'tour@tourguide.com'")
        }
    }

    fun validatePasswords(password: String, confirmPassword: String) {
        // passwords
        if (password.isBlank()) {
            throw Exception("Password cannot be empty.")
        }
        if (password.length < 6) {
            throw Exception("Password must have 6 symbols.")
        }
        if (password != confirmPassword) {
            throw Exception("Passwords are not matching!")
        }
    }

    fun validateCategoryFilter(category: String, radius: String) {
        if (!category.matches(charsOnly)) {
            throw Exception("Category must be only characters. Or characters are not ASCII only.")
        }
        if (category == TourConstants.DefaultCategory) {
            throw Exception("Category not chosen.")
        }
        validateRadius(radius)

    }

    fun validateRadius(radius: String) {
        if (radius.isBlank()) {
            throw Exception("Radius can't be empty.")
        }
        if (!radius.isDigitsOnly()) {
            throw Exception("Radius must be only digits.")
        }
        if (radius.toInt() > 10000) {
            throw Exception("Radius can't be larger than 10 kilometers.")
        }
    }
}