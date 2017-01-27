angular.module('BigBlueButton')
    .controller('UsersController', function ($http, $scope, AuthService) {
        $scope.user = AuthService.user;
        $scope.edit = false;
        $scope.buttonText = 'Create';
        $scope.modalText = 'Create User';
        $scope.notAllowed = true;
        getAuthority();

        /**
         * This function returns user details in the selected row
         * @param row - row containing details of user to be edited
         */
        $scope.rowHighlighted = function (row) {
            $scope.userSelectedRow = row;
        }

        $scope.tooltip = {
            "title": "",
            "checked": false
        };

        /**
         * This function gets all the admin users of the system and will be used to filter what normal user can or can't see
         */
        function getAuthority() {
            $scope.isSuperAdmin = false;
            for (var i = 0; i < $scope.user.authorities.length; i++) {
                var auth = $scope.user.authorities[i];
                if (auth.authority === 'SUPER_ADMIN') {
                    $scope.isSuperAdmin = true;
                }
            }
        };

        $scope.$watch('userSelectedRow', function () {
            if ($scope.userSelectedRow !== undefined) {
                var selected = $scope.users[$scope.userSelectedRow];
                if (selected.role === 'ADMIN' && !$scope.isSuperAdmin && selected.username !== $scope.user.name) {
                    $scope.notAllowed = true;
                } else {
                    $scope.notAllowed = false;
                }
            }
        });

        /**
         * This function gets all the users in the user table and
         * initializes component that will be used to edit user details
         */
        var init = function () {
            $http.get('api/users').success(function (res) {
                $scope.users = res;

                $scope.userForm.$setPristine();
                $scope.message = '';
                $scope.appUser = null;
                $scope.buttonText = 'Update';
                $scope.modalText = 'Update User';

            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        /**
         * This function initializes components for editing user details
         *
         * @param appUser - current user editing user details
         */
        $scope.initEdit = function (appUser) {
            $scope.edit = true;
            $scope.tooltip.checked = false;
            $scope.appUser = appUser;
            $scope.message = '';
            $scope.buttonText = 'Update';
            $scope.modalText = 'Update User';
        };

        /**
         *  This function initializes components for creating a user
         */
        $scope.initAddUser = function () {
            $scope.edit = false;
            $scope.appUser = null;
            $scope.userForm.$setPristine();
            $scope.message = '';
            $scope.buttonText = 'Create';
            $scope.modalText = 'Create User';
        };

        /**
         *  This function deletes a user when the delete action is selected
         *
         * @param appUser - current user of the system deleitng user details
         */
        $scope.deleteUser = function (appUser) {
            $http.delete('api/user/' + appUser.id).success(function (res) {
                $scope.message = "Deleted successfully.";
                init();
            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        /**
         * This function edits user details when user details are being managed
         */
        var editUser = function () {
            $http.put('api/user', $scope.appUser).success(function (res) {
                $scope.appUser = null;
                $scope.confirmPassword = null;
                $scope.userForm.$setPristine();
                $scope.message = "Updated successfully";
                init();
                $scope.closeModal();
            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        /**
         * This function creates a user when a user registers
         */
        var createUser = function () {
            if(!$scope.edit && ($scope.appUser.password === undefined || !$scope.appUser.password)){
                $scope.message = 'Please enter a valid password';
            } else {
                $http.post('api/user', $scope.appUser).success(function (res) {
                    $scope.appUser = null;
                    $scope.confirmPassword = null;
                    $scope.userForm.$setPristine();
                    $scope.message = "User Created";
                    init();
                    $scope.closeModal();
                }).error(function (error) {
                    $scope.message = error.message;
                });
            }
        };

        /**
         * This submit function calls editUser or createUser method based on whether
         * or not edit action was requested on submit
         */
        $scope.submit = function () {
            if ($scope.edit) {
                editUser();
            } else {
                createUser();
            }
        };
        init();
    });
