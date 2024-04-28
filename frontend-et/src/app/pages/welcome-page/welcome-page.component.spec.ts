import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WelcomePageComponent } from './welcome-page.component';
import {AuthService} from "@auth0/auth0-angular";
import {RouterTestingModule} from "@angular/router/testing";

describe('WelcomePageComponent', () => {
  let component: WelcomePageComponent;
  let fixture: ComponentFixture<WelcomePageComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['loginWithRedirect']);

    await TestBed.configureTestingModule({
      imports: [WelcomePageComponent, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    })
    .compileComponents();

    fixture = TestBed.createComponent(WelcomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display welcome message', () => {
    const welcomeMessage = fixture.nativeElement.querySelector('h1').textContent.trim();
    expect(welcomeMessage).toBe('Willkommen zu deinemBills Detective');
  });

  it('should call AuthService.loginWithRedirect when login button is clicked', () => {
    const loginButton = fixture.nativeElement.querySelector('#qsLoginBtn');
    loginButton.click();
    expect(authServiceSpy.loginWithRedirect).toHaveBeenCalled();
  });
});
