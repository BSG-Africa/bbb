angular.module('BigBlueButton')
    .controller('LoginController', function ($http, $rootScope, $scope, $state, AuthService, LoginService) {
        if($state.current.name == 'login'){
            $rootScope.$broadcast('RegisterAllowed');
        }
        /**
         * This function calls the login service to authenticate user parsing
         * the provided username and password
         */
        $scope.login = function () {
            LoginService.loginUser($scope.username, $scope.password, $scope);
        };
    });
