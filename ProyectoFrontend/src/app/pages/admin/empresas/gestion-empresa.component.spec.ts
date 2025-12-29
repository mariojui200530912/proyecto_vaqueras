import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionEmpresaComponent } from './gestion-empresa.component';

describe('GestionEmpresaComponent', () => {
  let component: GestionEmpresaComponent;
  let fixture: ComponentFixture<GestionEmpresaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionEmpresaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionEmpresaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
