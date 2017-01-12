angular.module('BigBlueButton')
    .controller('MeetingController', function ($http, $scope, AuthService, $state, $stateParams, $window, $rootScope, $timeout, Upload) {
        var edit = false;
        $scope.user = AuthService.user;
        $scope.name = $scope.user.principal.name;
        $scope.meetingName = $stateParams.meetingName;
        $scope.buttonText = 'Create';

        // App variable to show the uploaded response
        $scope.responseData = undefined;

        $scope.tooltip = {
            "title": "Click below to Upload the file or Drag and Drop the file below",
            "checked": false
        };

        $scope.$watch('file', function () {
            if ($scope.file != null) {
                $scope.upload($scope.file);
            }
        });

        $scope.rowHighlighted = function (row) {
            $scope.myMeetingsSelectedRow = row;
        };

        $scope.getUsersBySearchTerm = function (searchTerm) {
            if (searchTerm !== '' && typeof searchTerm === 'string') {
                var query = searchTerm.toLowerCase(),
                    emp = $scope.allUsers.filter(function(user){
                        return (user.role == 'ADMIN')
                    }),
                    employees = $.parseJSON(JSON.stringify(emp));

                var result = _.filter(employees, function (i) {
                    return ~i.name.toLowerCase().indexOf(query);
                });
                return result;
            }
            return null;
        };

        $scope.addMeeting = function () {
            edit = false;
            $scope.message = '';
            $scope.responseData = '';
            $scope.tooltip.checked = false;
            $scope.myMeetingsSelectedRow = undefined;
            $scope.meeting = $scope.myMeeting[$scope.myMeetingsSelectedRow];
            $scope.buttonText = 'Create';
        };

        $scope.editMeeting = function () {
            edit = true;
            $scope.message = '';
            $scope.responseData = '';
            $scope.tooltip.checked = false;
            $scope.meeting = $scope.myMeeting[$scope.myMeetingsSelectedRow];
            $scope.buttonText = 'Update';
        };

        // upload on file select or drop
        $scope.upload = function (file) {
            $scope.responseData = '';
            Upload.upload({
                url: 'api/meeting/upload',
                file: file
            }).then(function (resp) {
                $scope.meeting.defaultPresentationURL = resp.data.url;
                $scope.responseData = resp.data.response;
            }, function (resp) {
                $scope.responseData = 'Error: Failed to upload the file';
                console.log(resp);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope.responseData = 'Progress: ' + progressPercentage + '%';
            });
        };

        $scope.createMeeting = function () {
            if(!(typeof $scope.meeting.moderator == "object")){
                $scope.message = 'Please select a valid Moderator Name';
            } else if ($scope.meeting.id > 0) {
                $http.post('api/meeting/edit', $scope.meeting).success(function (res) {
                    $scope.closeModal();
                }).error(function (error) {
                    $scope.message = error.message;
                });
            } else {
                $scope.user = AuthService.user;
                $scope.meeting.createdBy = $scope.user.principal;
                $scope.meeting.status = "Not started";
                $http.post('api/meeting/create', $scope.meeting).success(function (res) {
                    getMyMeetings ();
                    $scope.closeModal();
                }).error(function (error) {
                    $scope.message = error.message;
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
                var selectedMeeting = $scope.myMeeting[$scope.myMeetingsSelectedRow];

                // Open new tab for the meeting
                var newTab = $window.open('', '_blank');

                // Create BBB meeting whenever user starts meeting
                $http.post('api/meeting/create', selectedMeeting).success(function (res) {
                    newTab.location.href = selectedMeeting.moderatorURL;
                }).error(function (error) {
                    $scope.message = error.message;
                });
            }
        };

        $scope.goToMeetingAsAttendee = function (data) {
            $scope.selectedRow = data;
            var selectedMeeting = $scope.availableMeetings[data];
            $scope.meetingName = selectedMeeting.name;

            var url = $state.href('loading-meeting', {meetingName: $scope.meetingName});
            var newTab = window.open(url, '_blank');

            $scope.redirectToMeeting(selectedMeeting, newTab);
        };


        $scope.redirectToMeeting = function (selectedMeeting, newTab) {
            if (selectedMeeting.status === 'Started') {
                var meetingId = selectedMeeting.meetingId;
                $http.get('invite', {params: {"fullName": $scope.name, "meetingId": meetingId}}).success(function (res) {
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
                        }, 4000);
                    }

                }).error(function (error) {
                    $scope.message = error.message;
                });
            }

        };

        function getAvailableMeetings () {
            var userId = $scope.user.principal.id;
            $http.get('api/meeting/available/' + userId).success(function (res) {
                $scope.availableMeetings = res;

                if ($scope.availableMeetings === undefined || $scope.availableMeetings.length == 0) {
                    $scope.isMeetingEmpty = true;
                }
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