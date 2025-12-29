import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionComisionComponent } from './gestion-comision.component';

describe('GestionComisionComponent', () => {
  let component: GestionComisionComponent;
  let fixture: ComponentFixture<GestionComisionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionComisionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionComisionComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
