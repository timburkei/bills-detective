import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardComponent } from './dashboard.component';
import {AuthService} from "@auth0/auth0-angular";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {of} from "rxjs";

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', {
      isAuthenticated$: of(true),
      user$: of({ email: 'test@mail.com' }),  // Mocked user data
      loginWithRedirect: () => {},
    });

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a grid with three cards', () => {
    const grid = fixture.nativeElement.querySelector('.grid');
    expect(grid).toBeTruthy();
    expect(grid.querySelectorAll('.list-card').length).toBe(3);
  });

  it('should show pop-up and close on button click', () => {
    // Überprüfen, ob das Pop-up angezeigt wird
    expect(fixture.nativeElement.querySelector('.alert')).toBeTruthy();

    // Den "OK"-Button klicken
    fixture.nativeElement.querySelector('button').click();
    fixture.detectChanges();

    // Überprüfen, ob das Pop-up geschlossen wurde
    expect(fixture.nativeElement.querySelector('.alert')).toBeFalsy();
    expect(component.showPopUp).toBeFalse();
  });

});
