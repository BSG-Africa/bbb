angular.module('BigBlueButton')
    .controller('CreateMeetingController', function ($http, $scope, AuthService, $state) {
        $scope.createMeeting = function () {
            $scope.additionalInfo = '';
            for (var i = 0; i < $scope.additionalInformationElemnt.length; i++) {
                $scope.additionalInfo = $scope.additionalInfo + $scope.additionalInformationElemnt[i].additionalInformation + ': ' + $scope.additionalInformationElemnt[i].answer + ',';
            }
            console.log($scope.additionalInfo);
            //$scope.meeting.agenda = $scope.additionalInfo;
            $scope.user = AuthService.user;
            // TODO : Ivhani Please remove this
            $scope.meeting.agenda = $scope.additionalInfo;
            $scope.meeting.createdBy = $scope.user.principal.id;
            $scope.meeting.status = "Not started";
            $http.post('api/meeting/create', $scope.meeting).success(function (res) {

                $scope.message = "Meeting creation successfull !";
                $state.go('meeting');
            }).error(function (error) {
                $scope.message = error.message;
            });
        };


        var counter = 0;
        $scope.additionalInformationElemnt = [{
            id: counter,
            additionalInformation: 'Item name (click to change)',
            answer: '',
            inline: true
        }];


        $scope.newItem = function ($event) {

            /*            $scope.additionalInfo = '1';
             for (var i = 0; i < $scope.additionalInformationElemnt.length; i++) {
             $scope.additionalInfo = $scope.additionalInfo + $scope.additionalInformationElemnt[i].additionalInformation + ': ' +$scope.additionalInformationElemnt[i].answer + ',';
             }
             console.log($scope.additionalInfo);*/


            counter++;
            $scope.additionalInformationElemnt.push({
                id: counter,
                additionalInformation: 'Item name (click to change)',
                answer: 'l',
                inline: true
            });
            $event.preventDefault();
        }
        /*        $scope.inlinef= function($event,inlinecontrol){
         var checkbox = $event.target;
         if(checkbox.checked){
         $('#'+ inlinecontrol).css('display','inline');
         }else{
         $('#'+ inlinecontrol).css('display','');
         }

         }
         $scope.showitems = function($event){
         $('#displayitems').css('visibility','none');
         }*/


    });