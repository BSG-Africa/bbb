angular.module('BigBlueButton')
    .controller('MeetingController', function ($http, $scope, AuthService, $state, $stateParams, $window, $rootScope, $timeout, Upload) {
        var edit = false;
        $scope.user = AuthService.user;
        $scope.name = $scope.user.principal.name;
        $scope.meetingName = $stateParams.meetingName;
        $scope.buttonText = 'Create';
        $scope.modalText = 'Create Meeting';

        // App variable to show the uploaded response
        $scope.responseData = undefined;

        $scope.tooltip = {
            "title": "Click below to Upload the file or Drag and Drop the file below",
            "checked": false
        };

        /**
         * This function used for initializing the upload of presentation file
         */
        $scope.$watch('file', function () {
            if ($scope.file != null) {
                $scope.upload($scope.file);
            }
        });

        /**
         * This function sets selected row for meeting item to be edited
         *
         * @param row - row containing details to be edited
         */
        $scope.rowHighlighted = function (row) {
            $scope.myMeetingsSelectedRow = row;
        };

        /**
         * This function searches for admin user using a provided search term.
         * If user exists return results, else return null.
         *
         * @param searchTerm - search term used for find a user by
         * @returns {*}
         */
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

        /**
         * This function initializes components that will be used to create a meeting
         */
        $scope.addMeeting = function () {
            edit = false;
            $scope.message = '';
            $scope.responseData = '';
            $scope.tooltip.checked = false;
            $scope.myMeetingsSelectedRow = undefined;
            $scope.meeting = $scope.myMeeting[$scope.myMeetingsSelectedRow];
            $scope.buttonText = 'Create';
            $scope.modalText = 'Create Meeting';
        };

        /**
         * This function initializes components that will be used to edit a meeting
         */
        $scope.editMeeting = function () {
            edit = true;
            $scope.message = '';
            $scope.responseData = '';
            $scope.tooltip.checked = false;
            $scope.meeting = $scope.myMeeting[$scope.myMeetingsSelectedRow];
            $scope.buttonText = 'Update';
            $scope.modalText = 'Update Meeting';
        };

        /**
         * This function used for the upload on file select or drop,
         * If file is uploaded successfully return file details,
         * else if upload failed return status message
         * else if file is still loading indicate progress of file upload
         *
         * @param file - file to be uploaded or dropped on meeting create or edit
         */
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

        /**
         * This function creates/edits a bbb meeting based on whether a meeting already exists
         * First a check is done to check that a valid moderator was selected
         *
         * If a meeting already exists a post http request is sent to edit meeting
         * If a meeting does not exist, current logged on user is set as a principal user,
         * as well as the createdBy and meeting status, then a meeting is created
         */
        $scope.createMeeting = function () {
            if(!(typeof $scope.meeting.moderator == "object")){
                $scope.message = 'Please select a valid Moderator Name';
            } else if ($scope.meeting.id > 0) {
                $scope.user = AuthService.user;
                $scope.meeting.modifiedBy = $scope.user.principal;
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

        /**
         *  This function deletes a selected meeting
         *  If a meeting is not selected, the current user will be prompted
         *  to select a tow to delete.
         *  Else a http delete request will be called taking selected meeting id as a parameter
         *  User meetings are then updated to reflect change
         */
        $scope.deleteMeeting = function () {

            if ($scope.myMeetingsSelectedRow === undefined) {
                $scope.message = 'Please select a row';
                $timeout(function () {
                    $scope.message = undefined;
                }, 4000);
            } else {
                $http.delete('api/meeting/delete/' + $scope.myMeeting[$scope.myMeetingsSelectedRow].id)
                    .success(function (res) {
                    $scope.message = "Success!";
                    var current = $state.current;
                    var params = angular.copy($stateParams);
                    $state.transitionTo(current, params, {reload: true, inherit: true, notify: true});

                }).error(function (error) {
                    $scope.message = error.message;
                });
            }
        };

        /**
         * This function goes to a selected BBB meeting the current user is moderating.
         * If a bbb meeting is not selected the current user will be prompted to select a meeting.
         * Else create a BBB meeting and redirect user to the meeting on BBB.
         * When moderator logout of the meeting the bbb meeting gets ended.
         */
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

        /**
         * This function allows user to join an available BBB meeting as an attendee by
         * Using selected meeting details to redirect attendee to BBB meeting page.
         * If a BBB meeting has not been started a attendee will be redirected to the
         * meeting loading page and redirects attendee to the BBB meeting once the
         * BBB meeting is started
         *
         * @param data - data of meeting to be attended as attendee
         */
        $scope.goToMeetingAsAttendee = function (data) {
            $scope.selectedRow = data;
            var selectedMeeting = $scope.availableMeetings[data];
            $scope.meetingName = selectedMeeting.name;

            var url = $state.href('loading-meeting', {meetingName: $scope.meetingName});
            var newTab = window.open(url, '_blank');

            $scope.redirectToMeeting(selectedMeeting, newTab);
        };

        /**
         * This function redirects user to the BBB meeting if the meeting is started
         * Else user is redirected to the meeting loading page until the BBB meeting
         * is started
         * @param selectedMeeting - BBB meeting the a user either starts as moderator
         *                          or joins as an attendee
         * @param newTab - new tab to be user for a BBB meeting or meeting loading page
         */
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

        /**
         * This function gets all available meetings for a user to join.
         * These are meetings not created or moderated by current user.
         */
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

        /**
         * This function gets all the meetings created or moderator by current user
         */
        function getMyMeetings () {
            var userId = $scope.user.principal.id;

            $http.get('api/meeting/' + userId).success(function (res) {
                $scope.myMeeting = res;
                $scope.message = '';

            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        /**
         * This function gets all the admin users of the system and will be used to filter what normal user can or can't see
         */
        function getAuthority() {
            $scope.isAdmin = false;
            for (var i = 0; i < $scope.user.authorities.length; i++) {
                var auth = $scope.user.authorities[i];
                if (auth.authority === 'ADMIN') {
                    $scope.isAdmin = true;
                }
            }
        };

        /**
         * This function gets all the users in a user table and it used in the type ahead component to find user quickly
         */
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