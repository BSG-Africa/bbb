angular.module('BigBlueButton')
    .controller('InviteController', function ($http, $scope, AuthService, $state, $stateParams, $location, $window, $timeout) {
        $scope.user = AuthService.user;

        $scope.name = '';
        $scope.meetingParam = $location.search().meetingID;

        $scope.submit = function () {
            $http.get('invite', {params:{"fullName": $scope.name, "meetingId": $scope.meetingParam}}).success(function (res) {
                $scope.message = '';
                if (res.meetingStatus === 'Started') {
                    navigateToURL(res.inviteURL);
                }else{
                    console.log(res);
                    var tempUser = {principal : {name : res.fullName}};
                    $state.go('loadingForNonUser', {user: tempUser});
                    $scope.user = tempUser;
                    console.log($scope.user.principal.name);
                    $scope.redirectToMeeting(res, res.inviteURL);
                }
            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        var navigateToURL = function (joinURL) {
            $window.location.href = joinURL;
        };

        $scope.redirectToMeeting = function (meetingInvite, newTab, joinURL) {
            if (meetingInvite.meetingStatus === 'Started') {
                navigateToURL(meetingInvite.inviteURL);
            }else{
                console.log(meetingInvite);
                console.log(meetingInvite);
                $http.get('invite', {params:{"fullName": $scope.name, "meetingId": $scope.meetingParam}}).success(function (res) {
                    $scope.message = '';
                    $timeout(function () {
                        $scope.redirectToMeeting(res, joinURL);
                    }, 2000);
                }).error(function (error) {
                    $scope.message = error.message;
                });

            }

        };

    });