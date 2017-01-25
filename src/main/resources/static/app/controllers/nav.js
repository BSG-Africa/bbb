angular.module('BigBlueButton')
// Creating the Angular Controller
    .controller('NavController', function ($http, $scope, AuthService, $state, $rootScope, $sessionStorage) {
        var res = $sessionStorage.loggedUser;
        $scope.registerLink = true;
        $scope.loginLink = false;

        if (res && res.authenticated) {
            $http.defaults.headers.common['Authorization'] = $sessionStorage.loggedAuth;
            $scope.user = $sessionStorage.loggedUser;
        }

        /**
         *  This function sets the user on the session to the authorised user
         */
        $scope.$on('LoginSuccessful', function () {
            $scope.user = AuthService.user;
        });

        /**
         *  This function clears the session when user logs out
         */
        $scope.$on('LogoutSuccessful', function () {
            $scope.user = null;
        });

        /**
         *  This function hides the register link when user has logged on to the system but unhide the login
         */
        $scope.$on('RegisterNotAllowed', function () {
            $scope.registerLink = false;
            $scope.loginLink = true;
        });

        /**
         * This function unhides the registration link when user is not logged on to the system but hide the login
         */
        $scope.$on('RegisterAllowed', function () {
            $scope.registerLink = true;
            $scope.loginLink = false;
        });

        /**
         *  This function hides the register and login link
         */
        $scope.$on('RegisterAndLoginNotAllowed', function () {
            $scope.registerLink = false;
            $scope.loginLink = false;
        });

        /**
         * This function redirects user to the default meeting page when a page is not found
         */
        $scope.$on('PageNotFound', function () {
            $state.go('meeting');
        });

        /**
         * This function redirects a user to the login lading page once a user has
         * successfully logged out of the system.
         */
        $scope.logout = function () {
            AuthService.user = null;
            $sessionStorage.loggedUser = null;
            $sessionStorage.loggedAuth = null;
            $rootScope.$broadcast('LogoutSuccessful');
            $state.go('login');
        };
    });
