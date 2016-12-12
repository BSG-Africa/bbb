angular.module('BigBlueButton')
    .controller('CreateMeetingController', function ($http, $scope, AuthService, $state) {
        $scope.createMeeting = function () {
            $scope.additionalInfo = '';
            for (var i = 0; i < $scope.additionalInformationElemnt.length; i++) {
                $scope.additionalInfo = $scope.additionalInfo + $scope.additionalInformationElemnt[i].additionalInformation + ': ' + $scope.additionalInformationElemnt[i].answer + ',';
            }
            $scope.user = AuthService.user;
            $scope.meeting.agenda = $scope.additionalInfo;
            $scope.meeting.createdBy = $scope.user.principal.id;
            $scope.meeting.status = "Not started";
            $http.post('/api/meeting/create', $scope.meeting).success(function (res) {

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
            counter++;
            $scope.additionalInformationElemnt.push({
                id: counter,
                additionalInformation: 'Item name (click to change)',
                answer: 'l',
                inline: true
            });
            $event.preventDefault();
        }

        $scope.getUsersBySearchTerm = function (searchTerm) {
            if (searchTerm !== '' && typeof searchTerm === 'string') {
                var query = searchTerm.toLowerCase(),
                    emp = $scope.allUsers,
                    employees = $.parseJSON(JSON.stringify(emp));

                var result = _.filter(employees, function (i) {
                    return ~i.name.toLowerCase().indexOf(query);
                });
                return result;
            }
            return null;
        };

        function getAllUsers() {
            $http.get('api/users').success(function (res) {
                $scope.allUsers = res;
            }).error(function (error) {
                $scope.message = error.message;
            });
        };
        getAllUsers();

    });