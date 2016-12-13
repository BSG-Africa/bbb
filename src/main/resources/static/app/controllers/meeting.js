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
            var a = $scope.meeting[$scope.myMeetingsSelectedRow].id;
            $http.delete('api/meeting/delete/' + $scope.meeting[$scope.myMeetingsSelectedRow].id).success(function (res) {
                $scope.deleteMessage = "Success!";
                var current = $state.current;
                var params = angular.copy($stateParams);
                $state.transitionTo(current, params, {reload: true, inherit: true, notify: true});

            }).error(function (error) {
                $scope.deleteMessage = error.message;
            });
        };

        $scope.goToMeetingAsModerator = function () {
            $http.post('api/meeting/start', $scope.meeting[$scope.myMeetingsSelectedRow]).success(function (res) {
                $scope.message = "Meeting start successfull !";
                $window.location.href = $scope.meeting[$scope.myMeetingsSelectedRow].moderatorURL;
            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        $scope.goToMeetingAsAttendee = function () {

            $window.location.href = $scope.meeting[$scope.myMeetingsSelectedRow].inviteURL;
        };

        $scope.shareLink = function () {
            var link = "mailto:"+ ''
                + "?subject=Shared%20Link: " + escape('Big Blue Button Session')
                + "&body=" + escape($scope.meeting[$scope.myMeetingsSelectedRow].inviteURL);

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