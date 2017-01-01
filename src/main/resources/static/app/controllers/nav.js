angular.module('BigBlueButton')
// Creating the Angular Controller
    .controller('NavController', function ($http, $scope, AuthService, $state, $rootScope, $sessionStorage) {
        var res = $sessionStorage.loggedUser;
        $scope.registerLink = true;
        if (res && res.authenticated) {
            $http.defaults.headers.common['Authorization'] = $sessionStorage.loggedAuth;
            $scope.user = $sessionStorage.loggedUser;
        }
        $scope.$on('LoginSuccessful', function () {
            $scope.user = AuthService.user;
        });
        $scope.$on('LogoutSuccessful', function () {
            $scope.user = null;
        });
        $scope.$on('RegisterAllowed', function () {
            $scope.registerLink = false;
        });
        $scope.logout = function () {
            AuthService.user = null;
            $sessionStorage.loggedUser = null;
            $sessionStorage.loggedAuth = null;
            $rootScope.$broadcast('LogoutSuccessful');
            $state.go('login');
        };
    });
