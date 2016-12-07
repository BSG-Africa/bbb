angular.module('BigBlueButton')
    .controller('CreateMeetingController', function ($http, $scope, AuthService) {
        $scope.createMeeting = function () {
            $http.post('/api/meeting/create', $scope.meeting).success(function (res) {

                $scope.message = "Meeting creation successfull !";
            }).error(function (error) {
                $scope.message = error.message;
            });
        };

    });