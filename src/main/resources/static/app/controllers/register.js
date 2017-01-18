angular.module('BigBlueButton')
    .controller('RegisterController', function ($http, $scope, $state, $rootScope, LoginService) {
        if($state.current.name == 'register'){
            $rootScope.$broadcast('RegisterNotAllowed');
        }

        /**
         * This function registers a new user and calls the login servcve to log user on
         * once registration is successful
         */
        $scope.submit = function () {
            $http.post('register', $scope.appUser).success(function (res) {
                $scope.register.$setPristine();
                $scope.message = "Registration successful !";
                LoginService.loginUser($scope.appUser.username, $scope.appUser.password, $scope);
                $scope.appUser = null;
                $scope.confirmPassword = null;
                $rootScope.$broadcast('RegisterAllowed');
            }).error(function (error) {
                $scope.message = error.message;
            });
        };
    });
