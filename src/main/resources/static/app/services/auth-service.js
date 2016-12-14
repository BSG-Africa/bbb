angular.module('BigBlueButton')
// Creating the Angular Service for storing logged user details
    .service('AuthService', function ($sessionStorage, $http) {
        var _user = $sessionStorage.loggedUser;
        if (_user && _user.authenticated) {
            $http.defaults.headers.common['Authorization'] = $sessionStorage.loggedAuth;
        }
        return {
            user: _user
        }
    });
