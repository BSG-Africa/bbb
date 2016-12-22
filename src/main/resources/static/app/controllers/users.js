angular.module('BigBlueButton')
// Creating the Angular Controller
    .controller('UsersController', function ($http, $scope, AuthService) {
        var edit = false;
        $scope.buttonText = 'Create';

        var init = function () {
            $http.get('api/users').success(function (res) {
                $scope.users = res;

                $scope.userForm.$setPristine();
                $scope.message = '';
                $scope.appUser = null;
                $scope.buttonText = 'Update';

            }).error(function (error) {
                $scope.message = error.message;
            });
        };
        $scope.initEdit = function (appUser) {
            edit = true;
            $scope.appUser = appUser;
            $scope.message = '';
            $scope.buttonText = 'Update';
        };
        $scope.initAddUser = function () {
            edit = false;
            $scope.appUser = null;
            $scope.userForm.$setPristine();
            $scope.message = '';
            $scope.buttonText = 'Create';
        };

        $scope.deleteUser = function (appUser) {
            $http.delete('api/user/' + appUser.id).success(function (res) {
                $scope.deleteMessage = "Success!";
                init();
            }).error(function (error) {
                $scope.deleteMessage = error.message;
            });
        };
        var editUser = function () {
            $http.put('api/user', $scope.appUser).success(function (res) {
                $scope.appUser = null;
                $scope.confirmPassword = null;
                $scope.userForm.$setPristine();
                $scope.message = "Editing Success";
                init();
            }).error(function (error) {
                $scope.message = error.message;
            });
        };
        var createUser = function () {
            $http.post('api/user', $scope.appUser).success(function (res) {
                $scope.appUser = null;
                $scope.confirmPassword = null;
                $scope.userForm.$setPristine();
                $scope.message = "User Created";
                init();
            }).error(function (error) {
                $scope.message = error.message;
            });
        };
        $scope.submit = function () {
            if (edit) {
                editUser();
            } else {
                createUser();
            }
        };
        init();
    });
