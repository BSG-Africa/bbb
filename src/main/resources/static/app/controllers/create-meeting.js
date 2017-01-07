angular.module('BigBlueButton')
    .controller('CreateMeetingController', function ($http, $scope, AuthService, $state, $stateParams, Upload) {
        if ($stateParams.meeting !== undefined) {
            $scope.meeting = $stateParams.meeting;
            $scope.allUsers = $stateParams.allUsers;
        }

        $scope.tooltip = {
            "title": "Click below to Upload the file or Drag and Drop the file below",
            "checked": false
        };

        // App variable to show the uploaded response
        $scope.responseData = undefined;

        $scope.$watch('file', function () {
            if ($scope.file != null) {
                $scope.upload($scope.file);
            }
        });

        // upload on file select or drop
        $scope.upload = function (file) {
            Upload.upload({
                url: 'upload',
                file: file
            }).then(function (resp) {
                $scope.meeting.defaultPresentationURL = resp.data.url;
                $scope.responseData = resp.data.response;
                console.log($scope.meeting);
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
                    $state.go('meeting');
                }).error(function (error) {
                    $scope.message = error.message;
                });
            } else {
                $scope.user = AuthService.user;
                $scope.meeting.createdBy = $scope.user.principal;
                $scope.meeting.status = "Not started";
                $http.post('api/meeting/create', $scope.meeting).success(function (res) {
                    $state.go('meeting');
                }).error(function (error) {
                    $scope.message = error.message;
                });
            }
        };

    });