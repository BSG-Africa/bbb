angular.module('BigBlueButton')
    .controller('InviteController', function ($http, $scope, AuthService, $state, $stateParams, $location, $window) {
        $scope.user = AuthService.user;

        $scope.name = '';
        $scope.meetingParam = $location.search().meetingID;

        $scope.submit = function () {
            $http.get('invite', {params:{"fullName": $scope.name, "meetingId": $scope.meetingParam}}).success(function (res) {
                $scope.message = '';
                navigateToURL(res.inviteURL);
            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        var navigateToURL = function (joinURL) {
            $window.location.href = joinURL;
        };

    });