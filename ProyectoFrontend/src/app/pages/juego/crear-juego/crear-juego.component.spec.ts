import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearJuegoComponent } from './crear-juego.component';

describe('CrearJuegoComponent', () => {
  let component: CrearJuegoComponent;
  let fixture: ComponentFixture<CrearJuegoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrearJuegoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CrearJuegoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
