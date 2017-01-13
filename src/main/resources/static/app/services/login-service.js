angular.module('BigBlueButton')
// Creating the Angular Service for logging the user
    .service('LoginService', function ($http, $state, $rootScope, $sessionStorage, AuthService) {
        var service = {
            loginUser: loginUser,
        }

        /**
         *  This function authenticates user by creating a base64 encoded string that is used to authenticate user
         *  Once the authentication is successful the user gets redirected to a meetings page.
         *  If the authentication fails or has an error, a message indicating authentication failure is displayed
         *
         * @param username  - username of the user to be authenticated
         * @param password - password of the user to be authenticated
         */
        function loginUser(username, password, $scope) {
            // creating base64 encoded String from user name and password
            var base64Credential = btoa(username + ':' + password);

            // calling GET request for getting the user details
            $http.get('user', {
                headers: {
                    // setting the Authorization Header
                    'Authorization': 'Basic ' + base64Credential
                }
            }).success(function (res) {
                $scope.password = null;
                if (res.authenticated) {
                    $scope.message = '';
                    // setting the same header value for all request calling from
                    // this application and adding user to session storage for refresh
                    $http.defaults.headers.common['Authorization'] = 'Basic ' + base64Credential;
                    AuthService.user = res;
                    $sessionStorage.loggedUser = res;
                    $sessionStorage.loggedAuth = 'Basic ' + base64Credential;
                    $rootScope.$broadcast('LoginSuccessful');
                    $state.go('meeting');
                } else {
                    $scope.message = 'Authentication Failed !';
                }
            }).error(function (error) {
                $scope.message = 'Authentication Failed !';
            });
        }

        return service;
    });