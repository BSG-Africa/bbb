angular.module('BigBlueButton')
    .controller('MeetingController', function ($http, $scope, AuthService, $state, $stateParams, $window) {
        $scope.user = AuthService.user;

        $scope.rowHighilited = function (row) {
            $scope.selectedRow = row;
        }

        $scope.rowHighlighted = function (row) {
            $scope.myMeetingsSelectedRow = row;
        }

        $scope.createMeeting = function () {
            $state.go('create-meeting');
        };

        $scope.deleteMeeting = function () {
            var a = $scope.myMeeting[$scope.myMeetingsSelectedRow].id;
            $http.delete('api/meeting/delete/' + $scope.myMeeting[$scope.myMeetingsSelectedRow].id).success(function (res) {
                $scope.deleteMessage = "Success!";
                var current = $state.current;
                var params = angular.copy($stateParams);
                $state.transitionTo(current, params, {reload: true, inherit: true, notify: true});

            }).error(function (error) {
                $scope.deleteMessage = error.message;
            });
        };

        $scope.goToMeetingAsModerator = function () {
            var meeting = $scope.myMeeting[$scope.myMeetingsSelectedRow];

            // Create BBB meeting whenever user starts meeting
            $http.post('api/meeting/create', meeting).success(function (res) {
                //$scope.message = "BBB meeting creation successfull !";
            }).error(function (error) {
                $scope.message = error.message;
            });

            // Open new tab for the meeting
            var newTab = $window.open('', '_blank');
            newTab.location.href = meeting.moderatorURL;

            // Wait 10 seconds then check if meeting is running
            setTimeout(function () {
                $http.post('api/meeting/start', meeting).success(function (res) {
                    $scope.message = "Meeting start successfull !";
                }).error(function (error) {
                    $scope.message = error.message;
                });
            }, 10000);
        };

        $scope.goToMeetingAsAttendee = function () {

            $window.location.href = $scope.myMeeting[$scope.myMeetingsSelectedRow].inviteURL;
        };

        $scope.shareLink = function () {
            var link = "mailto:"+ ''
                + "?subject=Shared%20Link: " + escape('Big Blue Button Session')
                + "&body=" + escape($scope.myMeeting[$scope.myMeetingsSelectedRow].inviteURL);

            window.location.href = link;
        };

        function getAvailableMeetings () {
            var userId = $scope.user.principal.id;
            $http.get('api/availableMeetings/' + userId).success(function (res) {
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

       getAvailableMeetings();
        getMyMeetings ();
    });