angular.module('BigBlueButton')
    .controller('MeetingController', function ($http, $scope, AuthService, $state, $stateParams, $window, $rootScope) {
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


        $scope.editMeeting = function () {
            $rootScope.$broadcast('EditMeeting');
            $state.go('edit-meeting/:id', {
                id: $scope.myMeeting[$scope.myMeetingsSelectedRow].id,
                allUsers: $scope.allUsers,
                meeting: $scope.myMeeting[$scope.myMeetingsSelectedRow]
            });
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

            // Wait 2 seconds then check if meeting is running
            setTimeout(function () {
                $http.post('api/meeting/start', meeting).success(function (res) {
                    $scope.message = "Meeting start successfull !";
                    newTab.location.href = meeting.moderatorURL;
                }).error(function (error) {
                    $scope.message = error.message;
                });
            }, 2000);
        };

        $scope.goToMeetingAsAttendee = function () {

            $window.location.href = $scope.myMeeting[$scope.myMeetingsSelectedRow].inviteURL;
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

        function getAuthority() {
            $scope.isAdmin = false;

            for (var i = 0; i < $scope.user.authorities.length; i++) {
                var auth = $scope.user.authorities[i];
                if (auth.authority === 'ADMIN') {
                    $scope.isAdmin = true;
                }
            }
        };

        function getAllUsers() {
            $http.get('api/users').success(function (res) {
                $scope.allUsers = res;
            }).error(function (error) {
                $scope.message = error.message;
            });
        };
        getAllUsers();
       getAvailableMeetings();
        getMyMeetings ();
        getAuthority();
    });