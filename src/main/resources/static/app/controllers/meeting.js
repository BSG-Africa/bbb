angular.module('BigBlueButton')
    .controller('MeetingController', function ($http, $scope, AuthService, $state) {


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

        function getAvailableMeetings () {
            $http.get('api/availableMeetings').success(function (res) {
                $scope.meeting = res;
                $scope.message = '';

            }).error(function (error) {
                $scope.message = error.message;
            });
        };



        $scope.getMyMeetings = function () {
            $state.go('myMeetings');
        };
        getAvailableMeetings();
    });