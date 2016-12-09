angular.module('BigBlueButton')
    .controller('InviteController', function ($http, $scope, AuthService, $state, $stateParams) {
        $scope.user = AuthService.user;

        $scope.createMeeting = function () {
            $state.go('create-meeting');
        };

        $scope.deleteMeeting = function () {
            var a = $scope.meeting[$scope.myMeetingsSelectedRow].id;
            $http.delete('/api/meeting/delete/' + $scope.meeting[$scope.myMeetingsSelectedRow].id).success(function (res) {
                $scope.deleteMessage = "Success!";
                var current = $state.current;
                var params = angular.copy($stateParams);
                $state.transitionTo(current, params, {reload: true, inherit: true, notify: true});

            }).error(function (error) {
                $scope.deleteMessage = error.message;
            });
        };

        function getAvailableMeetings () {
            $http.get('api/availableMeetings').success(function (res) {
                $scope.meeting = res;
                $scope.message = '';

            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        function getMyMeetings () {
            var userId = $scope.user.principal.id;

            $http.get('api/myMeetings/' + userId).success(function (res) {
                $scope.myMeeting = res;
                $scope.message = '';

            }).error(function (error) {
                $scope.message = error.message;
            });
        };
    });