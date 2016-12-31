angular.module('BigBlueButton')
// Creating the Angular Controller
    .controller('RegisterController', function ($http, $scope, $state, $rootScope) {
        if($state.current.name == 'register'){
            $rootScope.$broadcast('NavigatedOnRegister');
        }
        $scope.submit = function () {
            $http.post('register', $scope.appUser).success(function (res) {
                $scope.appUser = null;
                $scope.confirmPassword = null;
                $scope.register.$setPristine();
                $scope.message = "Registration successful !";
            }).error(function (error) {
                $scope.message = error.message;
            });
        };
    });
