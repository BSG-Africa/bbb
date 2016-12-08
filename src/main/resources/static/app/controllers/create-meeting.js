angular.module('BigBlueButton')
    .controller('CreateMeetingController', function ($http, $scope, AuthService, $state) {
        $scope.createMeeting = function () {
            $scope.user = AuthService.user;
            // TODO : Ivhani Please remove this
            $scope.meeting.createdBy = $scope.user.principal.name;
            $scope.meeting.status = "Not started";
            $http.post('/api/meeting/create', $scope.meeting).success(function (res) {

                $scope.message = "Meeting creation successfull !";
                $state.go('meeting');
            }).error(function (error) {
                $scope.message = error.message;
            });
        };

    });