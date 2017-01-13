angular.module('BigBlueButton')
    .controller('LoginController', function ($http, $scope, $state, AuthService, LoginService) {

        /**
         * This function calls the login service to authenticate user parsing
         * the provided username and password
         */
        $scope.login = function () {
            LoginService.loginUser($scope.username, $scope.password, $scope);
        };
    });
