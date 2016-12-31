angular.module('BigBlueButton')
// Creating the Angular Controller
    .controller('LoginController', function ($http, $scope, $state, AuthService, LoginService) {

        // method for login
        $scope.login = function () {
            LoginService.loginUser($scope.username, $scope.password, $scope);
        };
    });
