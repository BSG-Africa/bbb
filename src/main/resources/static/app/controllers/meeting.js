angular.module('BigBlueButton')
    .controller('MeetingController', function ($http, $scope, AuthService, $state) {

        $scope.meeting = [
            {
                name: 'Meeting 1',
                moderator: 'Kapeshi',
                status: 'Not Started'
            },
            {
                name: 'Meeting 1',
                moderator: 'Kapeshi',
                status: 'Started'
            }
        ];

        $scope.yourMeeting = [
            {
                name: 'Meeting 4',
                moderator: 'Kape4444shi',
                status: 'Not Started'
            },
            {
                name: 'Meeting r41',
                moderator: 'Kaper4shi',
                status: 'Started'
            }
        ];

        $scope.rowHighilited = function (row) {
            $scope.selectedRow = row;
        }

        $scope.rowHighlighted = function (row) {
            $scope.selectedRow2 = row;
        }

        $scope.createMeeting = function () {
            $state.go('create-meeting');
        };

    });