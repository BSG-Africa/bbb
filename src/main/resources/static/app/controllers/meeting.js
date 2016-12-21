angular.module('BigBlueButton')
    .controller('MeetingController', function ($http, $scope, AuthService, $state, $stateParams, $window, $rootScope, $timeout) {
        $scope.user = AuthService.user;

        $scope.rowHighlighted = function (row) {
            $scope.myMeetingsSelectedRow = row;
        }

        $scope.createMeeting = function () {
            $state.go('create-meeting');
        };


        $scope.editMeeting = function () {
            if ($scope.myMeetingsSelectedRow === undefined) {
                $scope.message = 'Please select a row';
                $timeout(function () {
                    $scope.message = undefined;
                }, 4000);
            } else {
                $state.go('edit-meeting/:id', {
                    id: $scope.myMeeting[$scope.myMeetingsSelectedRow].id,
                    allUsers: $scope.allUsers,
                    meeting: $scope.myMeeting[$scope.myMeetingsSelectedRow]
                });
            }
        };

        $scope.deleteMeeting = function () {

            if ($scope.myMeetingsSelectedRow === undefined) {
                $scope.message = 'Please select a row';
                $timeout(function () {
                    $scope.message = undefined;
                }, 4000);
            } else {
                $http.delete('api/meeting/delete/' + $scope.myMeeting[$scope.myMeetingsSelectedRow].id).success(function (res) {
                    $scope.message = "Success!";
                    var current = $state.current;
                    var params = angular.copy($stateParams);
                    $state.transitionTo(current, params, {reload: true, inherit: true, notify: true});

                }).error(function (error) {
                    $scope.message = error.message;
                });
            }
        };

        $scope.goToMeetingAsModerator = function () {
            if ($scope.myMeetingsSelectedRow === undefined) {
                $scope.message = 'Please select a row';
                $timeout(function () {
                    $scope.message = undefined;
                }, 4000);
            } else {
                var meeting = $scope.myMeeting[$scope.myMeetingsSelectedRow];

                // Open new tab for the meeting
                var newTab = $window.open('', '_blank');

                // Create BBB meeting whenever user starts meeting
                $http.post('api/meeting/create', meeting).success(function (res) {
                    newTab.location.href = meeting.moderatorURL;
                }).error(function (error) {
                    $scope.message = error.message;
                });
            }
        };

        $scope.goToMeetingAsAttendee = function (data) {
            $scope.selectedRow = data;
            var selectedMeeting = $scope.meeting[data];

            var url = $state.href('loading', {user: $scope.user});
            var newTab = window.open(url, '_blank');

            $scope.redirectToMeeting(selectedMeeting, newTab);
        };


        $scope.redirectToMeeting = function (selectedMeeting, newTab) {
            if (selectedMeeting.status === 'Started') {
                var meetingId = selectedMeeting.meetingId;
                var name = $scope.user.name;
                $http.get('invite', {params: {"fullName": name, "meetingId": meetingId}}).success(function (res) {
                    $scope.message = '';
                    newTab.location.href = res.inviteURL;

                }).error(function (error) {
                    $scope.message = error.message;
                });
            }
            else {
                $http.get('api/meeting/retrieve/' + selectedMeeting.id).success(function (res) {
                    if (selectedMeeting.status !== 'Started') {
                        $timeout(function () {
                            $scope.redirectToMeeting(res, newTab);
                        }, 2000);
                    }

                }).error(function (error) {
                    $scope.message = error.message;
                });
            }

        };

        var navigateToURL = function (url) {
            $window.open(url, '_blank');
        };

        function getAvailableMeetings () {
            var userId = $scope.user.principal.id;
            $http.get('api/meeting/available/' + userId).success(function (res) {
                $scope.meeting = res;
                $scope.message = '';

            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        function getMyMeetings () {
            var userId = $scope.user.principal.id;

            $http.get('api/meeting/' + userId).success(function (res) {
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