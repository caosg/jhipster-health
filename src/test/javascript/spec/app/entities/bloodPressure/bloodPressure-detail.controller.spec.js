'use strict';

describe('BloodPressure Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockBloodPressure, MockUser;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockBloodPressure = jasmine.createSpy('MockBloodPressure');
        MockUser = jasmine.createSpy('MockUser');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'BloodPressure': MockBloodPressure,
            'User': MockUser
        };
        createController = function() {
            $injector.get('$controller')("BloodPressureDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'healthApp:bloodPressureUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
