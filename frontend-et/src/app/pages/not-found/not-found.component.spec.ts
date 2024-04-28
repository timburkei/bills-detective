import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotFoundComponent } from './not-found.component';
import {RouterTestingModule} from "@angular/router/testing";

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotFoundComponent, RouterTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display a 404 message', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('p.text-base').textContent).toContain('404');
    expect(compiled.querySelector('h1').textContent).toContain('Seite nicht gefunden');
    expect(compiled.querySelector('p.mt-6').textContent).toContain(
      'Entschuldigung, wir konnten die angeforderte Seite nicht finden.'
    );
  });

  it('should have a link to the homepage', () => {
    const compiled = fixture.nativeElement;
    const link = compiled.querySelector('a.btn-primary');

    expect(link).toBeTruthy();
    expect(link.getAttribute('routerLink')).toBe('/');
  });
});
